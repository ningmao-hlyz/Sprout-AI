package top.ningmao.myspring.ai.vectorstore;

import redis.clients.jedis.search.FTSearchParams;
import redis.clients.jedis.search.IndexOptions;
import redis.clients.jedis.search.Schema;
import top.ningmao.myspring.ai.document.Document;
import top.ningmao.myspring.ai.embedding.EmbeddingModel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


/**
 * RedisVectorStore - 基于 Redis Stack 的向量存储实现
 * <p>
 * 使用 Redis Stack = Redis + RediSearch + RedisJSON
 * <p>
 * 核心特性：
 * - 使用 Redis Hash 存储文档数据（JSON 格式）
 * - 使用 RediSearch VECTOR 字段建立向量索引（HNSW 算法）
 * - 使用 FT.SEARCH 执行 KNN 向量相似度搜索（Cosine 距离）
 * <p>
 * 参考：
 * - Spring AI RedisVectorStore: https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
 * - Redis Vector Search: https://redis.io/docs/interact/search-and-query/search/vectors/
 * 
 * @author 宁猫
 * @since 2025-12-04
 */
public class RedisVectorStore implements VectorStore {

    private final JedisPooled jedis;
    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper;

    // 配置
    private final String indexName;
    private final String prefix;
    private final int dimensions;

    private static final String DEFAULT_INDEX_NAME = "sprout-idx";
    private static final String DEFAULT_PREFIX = "doc:";
    private static final int DEFAULT_DIMENSIONS = 768;

    public RedisVectorStore(EmbeddingModel embeddingModel) {
        this(embeddingModel, "localhost", 6379, DEFAULT_INDEX_NAME, DEFAULT_PREFIX, DEFAULT_DIMENSIONS);
    }

    public RedisVectorStore(EmbeddingModel embeddingModel, String host, int port,
                            String indexName, String prefix, int dimensions) {
        this.embeddingModel = embeddingModel;
        this.jedis = new JedisPooled(host, port);
        this.indexName = indexName;
        this.prefix = prefix;
        this.dimensions = dimensions;
        this.objectMapper = new ObjectMapper();

        initializeIndex();
    }

    /**
     * 初始化索引（使用 Jedis 原生对象构建，更安全）
     */
    private void initializeIndex() {
        try {
            // 检查索引是否存在
            try {
                jedis.ftInfo(indexName);
                return;
            } catch (Exception e) {
                // 忽略异常，视为不存在
            }

            // 定义 Schema
            // 注意：为了支持 Metadata 过滤，这里应该动态添加 Metadata 字段
            // 这里仅作为示例，添加了基础字段
            Schema schema = new Schema()
                    .addTagField("id")
                    .addTextField("content", 1.0)
                    .addVectorField("embedding", Schema.VectorField.VectorAlgo.HNSW,
                            Map.of(
                                    "TYPE", "FLOAT32",
                                    "DIM", dimensions,
                                    "DISTANCE_METRIC", "COSINE"
                            ));

            IndexOptions indexOptions = IndexOptions.defaultOptions()
                    .setDefinition(new IndexOptions.Definition()
                            .setPrefixes(new String[]{prefix}));

            jedis.ftCreate(indexName, indexOptions, schema);

        } catch (Exception e) {
            throw new RuntimeException("Redis Vector 索引初始化失败", e);
        }
    }

    @Override
    public void add(List<Document> documents) {
        if (documents == null || documents.isEmpty()) return;

        // 使用 Pipeline 批量执行，极大提高写入性能
        try (Pipeline pipeline = jedis.pipelined()) {
            for (Document doc : documents) {
                String key = prefix + doc.getId();
                float[] embedding = embeddingModel.embed(doc);
                byte[] vectorBytes = floatArrayToBytes(embedding);

                // 1. 存文本和元数据
                Map<String, String> fields = new HashMap<>();
                fields.put("id", doc.getId());
                fields.put("content", doc.getContent());

                // 将 Metadata 序列化存入一个字段，或者扁平化存储
                if (doc.getMetadata() != null) {
                    fields.put("metadata", objectMapper.writeValueAsString(doc.getMetadata()));
                }

                pipeline.hset(key, fields);

                // 2. 存向量 (因为 hset(String, Map<String,String>) 不支持 byte[] value，需单独设)
                pipeline.hset(key.getBytes(StandardCharsets.UTF_8),
                        "embedding".getBytes(StandardCharsets.UTF_8),
                        vectorBytes);
            }
            pipeline.sync(); // 执行批量提交
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(List<String> idList) {
        if (idList == null || idList.isEmpty()) return;

        String[] keys = idList.stream()
                .map(id -> prefix + id)
                .toArray(String[]::new);

        jedis.del(keys); // del 本身支持批量
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        try {
            float[] queryEmbedding = embeddingModel.embed(request.getQuery());
            byte[] queryBytes = floatArrayToBytes(queryEmbedding);

            // 构建 KNN 查询: *=>[KNN k @embedding $BLOB AS __vector_score]
            String queryStr = "*=>[KNN " + request.getTopK() + " @embedding $BLOB AS __vector_score]";

            // 使用 Jedis 的 params 构建器
            FTSearchParams params = new FTSearchParams()
                    .addParam("BLOB", queryBytes)
                    .returnFields("id", "content", "metadata", "__vector_score") // 指定返回字段
                    .dialect(2); // 必须是 dialect 2

            var searchResult = jedis.ftSearch(indexName, queryStr, params);

            return searchResult.getDocuments().stream().map(doc -> {
                String id = doc.getString("id");
                String content = doc.getString("content");
                // Redis 返回的是 Distance (0-2), 需要转换为 Similarity
                double distance = Double.parseDouble(doc.getString("__vector_score"));
                // Cosine Distance 范围 0 到 2 (0是完全一样，1是正交，2是相反)
                // 简单的相似度转换: 1 - (distance / 2) 或者 仅处理 0-1 范围
                double score = 1 - distance;

                // 恢复 metadata
                Map<String, Object> metadata = new HashMap<>();
                String metaJson = doc.getString("metadata");
                if (metaJson != null) {
                    try {
                        metadata = objectMapper.readValue(metaJson, Map.class);
                    } catch (Exception e) { /* ignore */ }
                }

                return Document.builder()
                        .id(id)
                        .content(content)
                        .metadata(metadata)
                        .score(score)
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public int size() {
        try {
            Map<String, Object> info = jedis.ftInfo(indexName);
            return Integer.parseInt(info.getOrDefault("num_docs", "0").toString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 使用 SCAN 命令分批删除，避免阻塞
     */
    public void clear() {
        String cursor = "0";
        ScanParams scanParams = new ScanParams().match(prefix + "*");

        do {
            var scanResult = jedis.scan(cursor, scanParams);
            List<String> keys = scanResult.getResult();
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
            cursor = scanResult.getCursor();
        } while (!"0".equals(cursor));

    }

    public void close() {
        jedis.close();
    }

    private byte[] floatArrayToBytes(float[] vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float value : vector) {
            buffer.putFloat(value);
        }
        return buffer.array();
    }
}