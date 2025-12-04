package top.ningmao.myspring.ai.vectorstore;

import org.junit.jupiter.api.*;
import top.ningmao.myspring.ai.document.Document;
import top.ningmao.myspring.ai.embedding.EmbeddingModel;
import top.ningmao.myspring.ai.huggingface.HuggingFaceEmbeddingModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * VectorStore 集成测试类
 * <p>
 * 测试 SimpleVectorStore 和 RedisVectorStore 的完整功能
 * 
 * @author 宁猫
 * @since 2025-12-03
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VectorStoreIntegrationTest {
    
    private static EmbeddingModel embeddingModel;
    private VectorStore vectorStore;
    
    @BeforeAll
    public static void setUpClass() {
        // 初始化 Embedding 模型（所有测试共享）
        embeddingModel = new HuggingFaceEmbeddingModel();
        System.out.println("\n========== VectorStore 集成测试 ==========");
        System.out.println("Embedding Model: HuggingFaceEmbeddingModel");
    }
    
    // ========== SimpleVectorStore 测试 ==========
    
    @Nested
    @DisplayName("SimpleVectorStore 测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SimpleVectorStoreTests {
        
        @BeforeEach
        void setUp() {
            vectorStore = new SimpleVectorStore(embeddingModel);
            System.out.println("\n===== 使用 SimpleVectorStore =====");
        }
        
        @Test
        @Order(1)
        @DisplayName("测试1：添加文档")
        void testAddDocuments() {
            List<Document> documents = List.of(
                    new Document("Spring Framework 是一个企业级 Java 开发框架"),
                    new Document("Spring Boot 简化了 Spring 应用的配置和部署"),
                    new Document("Spring Cloud 提供了微服务开发的工具集")
            );
            
            vectorStore.add(documents);
            assertThat(vectorStore.size()).isEqualTo(3);
            
            System.out.println(" 成功添加 3 个文档");
        }
        
        @Test
        @Order(2)
        @DisplayName("测试2：相似性搜索")
        void testSimilaritySearch() {
            List<Document> documents = List.of(
                    new Document("Spring Framework 是 Java 开发框架"),
                    new Document("Python Django 是 Web 开发框架"),
                    new Document("React 是前端 UI 库"),
                    new Document("Spring Boot 简化 Spring 开发")
            );
            vectorStore.add(documents);
            
            List<Document> results = vectorStore.similaritySearch("Spring 框架");
            
            assertThat(results).isNotEmpty();
            System.out.println("找到 " + results.size() + " 个相似文档");
            results.forEach(doc -> 
                System.out.printf("  [%.3f] %s%n", doc.getScore(), doc.getContent())
            );
            
            System.out.println(" 相似性搜索测试通过");
        }
        
        @Test
        @Order(3)
        @DisplayName("测试3：SearchRequest 高级搜索")
        void testSearchWithRequest() {
            List<Document> documents = List.of(
                    new Document("Redis 是内存数据库"),
                    new Document("MySQL 是关系型数据库"),
                    new Document("MongoDB 是文档数据库"),
                    new Document("Redis 支持持久化"),
                    new Document("PostgreSQL 是开源数据库")
            );
            vectorStore.add(documents);
            
            SearchRequest request = SearchRequest.builder()
                    .query("Redis 数据库")
                    .topK(2)
                    .similarityThreshold(0.6)
                    .build();
            
            List<Document> results = vectorStore.similaritySearch(request);
            
            assertThat(results).isNotEmpty();
            assertThat(results.size()).isLessThanOrEqualTo(2);
            
            System.out.println("Top-2 结果:");
            results.forEach(doc -> 
                System.out.printf("  [%.3f] %s%n", doc.getScore(), doc.getContent())
            );
            
            System.out.println(" 高级搜索测试通过");
        }
        
        @Test
        @Order(4)
        @DisplayName("测试4：删除文档")
        void testDeleteDocuments() {
            Document doc1 = Document.builder().id("doc-1").content("文档 1").build();
            Document doc2 = Document.builder().id("doc-2").content("文档 2").build();
            Document doc3 = Document.builder().id("doc-3").content("文档 3").build();
            
            vectorStore.add(List.of(doc1, doc2, doc3));
            assertThat(vectorStore.size()).isEqualTo(3);
            
            vectorStore.delete(List.of("doc-2"));
            assertThat(vectorStore.size()).isEqualTo(2);
            
            System.out.println(" 删除文档测试通过");
        }
        
        @Test
        @Order(5)
        @DisplayName("测试5：持久化（保存和加载）")
        void testPersistence() throws IOException {
            SimpleVectorStore simpleStore = (SimpleVectorStore) vectorStore;
            
            List<Document> documents = List.of(
                    new Document("持久化测试文档 1"),
                    new Document("持久化测试文档 2")
            );
            simpleStore.add(documents);
            
            // 保存到文件
            String filename = "test-vector-store.json";
            simpleStore.save(filename);
            
            // 创建新实例并加载
            SimpleVectorStore newStore = new SimpleVectorStore(embeddingModel);
            newStore.load(filename);
            
            assertThat(newStore.size()).isEqualTo(2);
            
            System.out.println(" 持久化测试通过");
            
            // 清理测试文件
            new java.io.File(filename).delete();
        }
        
        @Test
        @Order(6)
        @DisplayName("测试6：完整 RAG 流程")
        void testCompleteRAGFlow() {
            // 1. 准备知识库
            List<Document> knowledgeBase = List.of(
                    Document.builder()
                            .content("Spring Framework 是一个开源的 Java 应用框架")
                            .metadata("category", "framework")
                            .build(),
                    Document.builder()
                            .content("Spring Boot 提供了自动配置功能")
                            .metadata("category", "framework")
                            .build(),
                    Document.builder()
                            .content("Spring Cloud 用于构建微服务")
                            .metadata("category", "framework")
                            .build(),
                    Document.builder()
                            .content("Java 是一种面向对象的编程语言")
                            .metadata("category", "language")
                            .build()
            );
            
            // 2. 向量化并存储
            vectorStore.add(knowledgeBase);
            
            // 3. 用户问题
            String userQuestion = "如何简化 Spring 应用的配置？";
            
            // 4. 检索相关文档
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(userQuestion)
                    .topK(2)
                    .similarityThreshold(0.5)
                    .build();
            
            List<Document> relevantDocs = vectorStore.similaritySearch(searchRequest);
            
            assertThat(relevantDocs).isNotEmpty();
            assertThat(relevantDocs.size()).isLessThanOrEqualTo(2);
            
            // 5. 构建增强提示词
            StringBuilder context = new StringBuilder("参考以下信息回答问题：\n\n");
            relevantDocs.forEach(doc -> 
                context.append("- ").append(doc.getContent()).append("\n")
            );
            context.append("\n问题：").append(userQuestion);
            
            System.out.println("\n=== RAG 流程演示 ===");
            System.out.println("用户问题：" + userQuestion);
            System.out.println("\n检索到的相关文档：");
            relevantDocs.forEach(doc ->
                System.out.printf("  [%.3f] %s%n", doc.getScore(), doc.getContent())
            );
            System.out.println("\n增强提示词：");
            System.out.println(context);
            
            System.out.println(" 完整 RAG 流程测试通过");
        }
    }
    
    // ========== RedisVectorStore 测试（Redis Stack）==========
    
    @Nested
    @DisplayName("RedisVectorStore 测试（Redis Stack）")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RedisVectorStoreTests {
        
        private RedisVectorStore redisStore;
        
        @BeforeEach
        void setUp() {
            try {
                redisStore = new RedisVectorStore(embeddingModel);
                vectorStore = redisStore;
                redisStore.clear(); // 清空测试数据
                System.out.println("\n===== 使用 RedisVectorStore (Redis Stack) =====");
            } catch (Exception e) {
                System.out.println(" Redis Stack 未运行，跳过 Redis 测试");
                Assumptions.assumeTrue(false, "Redis Stack 服务未运行");
            }
        }
        
        @AfterEach
        void tearDown() {
            if (redisStore != null) {
                redisStore.clear();
                redisStore.close();
            }
        }
        
        @Test
        @Order(1)
        @DisplayName("测试1：Redis Stack 添加文档并自动创建 HNSW 索引")
        void testRedisAddDocuments() {
            List<Document> documents = List.of(
                    new Document("Redis Stack 集成了 RediSearch 和 RedisJSON"),
                    new Document("RediSearch 支持全文搜索和向量搜索"),
                    new Document("HNSW 算法提供高效的 KNN 搜索")
            );
            
            vectorStore.add(documents);
            assertThat(vectorStore.size()).isEqualTo(3);
            
            System.out.println(" Redis Stack HNSW 索引创建成功");
        }
        
        @Test
        @Order(2)
        @DisplayName("测试2：Redis Stack KNN 向量搜索")
        void testRedisVectorSearch() {
            List<Document> documents = List.of(
                    new Document("Redis Stack 是高性能向量数据库"),
                    new Document("MySQL 是传统关系型数据库"),
                    new Document("MongoDB 是 NoSQL 文档数据库"),
                    new Document("Redis 支持 HNSW 向量索引")
            );
            vectorStore.add(documents);
            
            // 测试 KNN 搜索
            SearchRequest request = SearchRequest.builder()
                    .query("Redis 向量搜索")
                    .topK(2)
                    .similarityThreshold(0.5)
                    .build();
            
            List<Document> results = vectorStore.similaritySearch(request);
            
            assertThat(results).isNotEmpty();
            assertThat(results.size()).isLessThanOrEqualTo(2);
            
            System.out.println("找到 Top-" + results.size() + " 相似文档（HNSW + Cosine）:");
            results.forEach(doc -> 
                System.out.printf("  [%.3f] %s%n", doc.getScore(), doc.getContent())
            );
            
            System.out.println(" Redis Stack KNN 搜索测试通过");
        }
        
        @Test
        @Order(3)
        @DisplayName("测试3：Redis Stack 数据持久化")
        void testRedisPersistence() {
            List<Document> documents = List.of(
                    new Document("持久化测试文档 1"),
                    new Document("持久化测试文档 2")
            );
            vectorStore.add(documents);
            
            int sizeBefore = vectorStore.size();
            
            // 关闭并重新创建连接（数据仍然存在）
            redisStore.close();
            redisStore = new RedisVectorStore(embeddingModel);
            
            int sizeAfter = redisStore.size();
            
            assertThat(sizeAfter).isEqualTo(sizeBefore);
            
            System.out.println(" Redis Stack 持久化测试通过（RDB/AOF）");
        }
        
        @Test
        @Order(4)
        @DisplayName("测试4：Redis Stack 大规模向量搜索性能")
        void testRedisPerformance() {
            // 添加更多文档测试性能
            List<Document> documents = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                documents.add(new Document("测试文档 " + i + ": Spring Boot 微服务开发"));
            }
            
            long startTime = System.currentTimeMillis();
            vectorStore.add(documents);
            long addTime = System.currentTimeMillis() - startTime;
            
            startTime = System.currentTimeMillis();
            List<Document> results = vectorStore.similaritySearch("Spring 微服务");
            long searchTime = System.currentTimeMillis() - startTime;
            
            assertThat(vectorStore.size()).isEqualTo(20);
            assertThat(results).isNotEmpty();
            
            System.out.println("性能测试结果:");
            System.out.println("  添加 20 个文档耗时: " + addTime + " ms");
            System.out.println("  向量搜索耗时: " + searchTime + " ms");
            System.out.println(" Redis Stack 性能测试通过");
        }
    }
}
