package top.ningmao.myspring.ai.vectorstore;

import top.ningmao.myspring.ai.document.Document;
import top.ningmao.myspring.ai.embedding.EmbeddingModel;

import cn.hutool.json.JSONUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimpleVectorStore - 简单的内存向量存储实现
 * <p>
 * 特性：
 * - 使用 ConcurrentHashMap 在内存中存储向量
 * - 支持余弦相似度搜索
 * - 支持保存/加载到 JSON 文件
 * - 线程安全
 * 
 * @author 宁猫
 * @since 2025-12-03
 */
public class SimpleVectorStore implements VectorStore {
    
    /**
     * Embedding 模型
     */
    private final EmbeddingModel embeddingModel;
    
    /**
     * 向量存储（文档 ID -> 向量内容）
     */
    private final Map<String, VectorContent> store;
    
    /**
     * 构造函数
     */
    public SimpleVectorStore(EmbeddingModel embeddingModel) {
        if (embeddingModel == null) {
            throw new IllegalArgumentException("EmbeddingModel 不能为 null");
        }
        this.embeddingModel = embeddingModel;
        this.store = new ConcurrentHashMap<>();
    }
    
    @Override
    public void add(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("文档列表不能为空");
        }
        
        for (Document document : documents) {
            // 生成向量
            float[] embedding = embeddingModel.embed(document);
            
            // 存储
            VectorContent content = new VectorContent(
                    document.getId(),
                    document.getContent(),
                    document.getMetadata(),
                    embedding
            );
            store.put(document.getId(), content);
        }
    }
    
    @Override
    public void delete(List<String> idList) {
        if (idList != null) {
            idList.forEach(store::remove);
        }
    }
    
    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        if (request == null || request.getQuery() == null) {
            throw new IllegalArgumentException("搜索请求不能为 null");
        }
        
        // 生成查询向量
        float[] queryEmbedding = embeddingModel.embed(request.getQuery());
        
        // 计算相似度并排序
        return store.values().stream()
                // 计算相似度
                .map(content -> {
                    double similarity = cosineSimilarity(queryEmbedding, content.getEmbedding());
                    return content.toDocument(similarity);
                })
                // 过滤相似度阈值
                .filter(doc -> doc.getScore() >= request.getSimilarityThreshold())
                // 降序排序
                .sorted(Comparator.comparing(Document::getScore).reversed())
                // 限制返回数量
                .limit(request.getTopK())
                .toList();
    }
    
    @Override
    public int size() {
        return store.size();
    }
    
    /**
     * 保存到文件（JSON 格式）
     * 
     * @param filename 文件路径
     * @throws IOException IO 异常
     */
    public void save(String filename) throws IOException {
        File file = new File(filename);
        
        // 序列化为 JSON
        String json = JSONUtil.toJsonPrettyStr(store);
        
        // 写入文件
        Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);
        
        System.out.println(" 向量存储已保存到: " + filename);
        System.out.println("   文档数量: " + store.size());
    }
    
    /**
     * 从文件加载（JSON 格式）
     * 
     * @param filename 文件路径
     * @throws IOException IO 异常
     */
    @SuppressWarnings("unchecked")
    public void load(String filename) throws IOException {
        File file = new File(filename);
        
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filename);
        }
        
        // 读取文件
        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        
        // 反序列化
        Map<String, VectorContent> loadedStore = JSONUtil.toBean(
                json, 
                Map.class
        );
        
        // 清空并加载新数据
        store.clear();
        
        // 由于 JSON 反序列化的限制，需要手动转换
        loadedStore.forEach((key, value) -> {
            // 这里需要处理 JSON 反序列化后的类型转换
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                
                String id = (String) map.get("id");
                String content = (String) map.get("content");
                
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) map.getOrDefault("metadata", new HashMap<>());
                
                // 转换 embedding 数组
                @SuppressWarnings("unchecked")
                List<Number> embeddingList = (List<Number>) map.get("embedding");
                float[] embedding = new float[embeddingList.size()];
                for (int i = 0; i < embeddingList.size(); i++) {
                    embedding[i] = embeddingList.get(i).floatValue();
                }
                
                VectorContent vectorContent = new VectorContent(id, content, metadata, embedding);
                store.put(key, vectorContent);
            }
        });
        
        System.out.println(" 向量存储已从文件加载: " + filename);
        System.out.println("   文档数量: " + store.size());
    }
    
    /**
     * 计算余弦相似度
     * 
     * @param vectorX 向量 X
     * @param vectorY 向量 Y
     * @return 余弦相似度（0-1）
     */
    private double cosineSimilarity(float[] vectorX, float[] vectorY) {
        if (vectorX == null || vectorY == null) {
            throw new IllegalArgumentException("向量不能为 null");
        }
        if (vectorX.length != vectorY.length) {
            throw new IllegalArgumentException("向量维度必须相同");
        }
        
        float dotProduct = 0.0f;
        float normX = 0.0f;
        float normY = 0.0f;
        
        for (int i = 0; i < vectorX.length; i++) {
            dotProduct += vectorX[i] * vectorY[i];
            normX += vectorX[i] * vectorX[i];
            normY += vectorY[i] * vectorY[i];
        }
        
        if (normX == 0 || normY == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(normX) * Math.sqrt(normY));
    }
    
    /**
     * 向量内容（内部类）
     */
    private static class VectorContent {
        private final String id;
        private final String content;
        private final Map<String, Object> metadata;
        private final float[] embedding;
        
        public VectorContent(String id, String content, Map<String, Object> metadata, float[] embedding) {
            this.id = id;
            this.content = content;
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
            this.embedding = embedding;
        }
        
        public String getId() {
            return id;
        }
        
        public String getContent() {
            return content;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
        
        public float[] getEmbedding() {
            return embedding;
        }
        
        /**
         * 转换为 Document（带相似度分数）
         */
        public Document toDocument(double score) {
            return Document.builder()
                    .id(id)
                    .content(content)
                    .metadata(metadata)
                    .score(score)
                    .build();
        }
    }
}
