package top.ningmao.myspring.ai.advisor;

import org.junit.jupiter.api.*;
import top.ningmao.myspring.ai.chat.advisor.QuestionAnswerAdvisor;
import top.ningmao.myspring.ai.chat.client.ChatClient;
import top.ningmao.myspring.ai.deepseek.DeepSeekChatModel;
import top.ningmao.myspring.ai.document.Document;
import top.ningmao.myspring.ai.embedding.EmbeddingModel;
import top.ningmao.myspring.ai.huggingface.HuggingFaceEmbeddingModel;
import top.ningmao.myspring.ai.vectorstore.RedisVectorStore;
import top.ningmao.myspring.ai.vectorstore.SimpleVectorStore;
import top.ningmao.myspring.ai.vectorstore.VectorStore;

import java.util.List;

/**
 * QuestionAnswerAdvisor 测试类
 * <p>
 * 测试 RAG 检索增强功能
 * 注意：需要设置 DEEPSEEK_API_KEY 环境变量
 *
 * @author 宁猫
 * @since 2025-12-04
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionAnswerAdvisorTest {

    private static EmbeddingModel embeddingModel;
    private static VectorStore vectorStore;
    private static DeepSeekChatModel chatModel;

    @BeforeAll
    public static void setUpClass() {
        System.out.println("\n========== QuestionAnswerAdvisor RAG 测试 ==========");
        
        // 1. 初始化 Embedding 模型
        embeddingModel = new HuggingFaceEmbeddingModel();
        
        // 2. 初始化 VectorStore
        vectorStore = new SimpleVectorStore(embeddingModel);
//        vectorStore = new RedisVectorStore(embeddingModel);
        
        // 3. 准备知识库文档
        List<Document> knowledgeBase = List.of(
            Document.builder()
                .content("Spring Framework 是一个开源的 Java 企业级应用框架，提供了 IoC 容器、AOP、事务管理等核心功能。")
                .metadata("category", "framework")
                .metadata("topic", "spring")
                .build(),
            Document.builder()
                .content("Spring Boot 是基于 Spring Framework 的快速开发框架，提供了自动配置、起步依赖、内嵌服务器等特性，极大简化了 Spring 应用的开发和部署。")
                .metadata("category", "framework")
                .metadata("topic", "spring-boot")
                .build(),
            Document.builder()
                .content("Redis 是一个高性能的内存数据库，支持多种数据结构，常用于缓存、消息队列、分布式锁等场景。")
                .metadata("category", "database")
                .metadata("topic", "redis")
                .build(),
            Document.builder()
                .content("Redis Stack 集成了 RediSearch、RedisJSON、RedisGraph 等模块，提供了全文搜索、向量搜索、图数据库等高级功能。")
                .metadata("category", "database")
                .metadata("topic", "redis-stack")
                .build()
        );
        
        // 4. 向量化并存储知识库
        vectorStore.add(knowledgeBase);
        System.out.println("✓ 知识库已加载: " + vectorStore.size() + " 个文档");
        
        // 5. 初始化 ChatModel
        String apiKey = System.getenv("DEEPSEEK_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println(" 未设置 DEEPSEEK_API_KEY，跳过 ChatModel 测试");
            chatModel = null;
        } else {
            chatModel = new DeepSeekChatModel(apiKey);
            System.out.println("✓ DeepSeek ChatModel 已初始化");
        }
    }

    @Test
    @Order(1)
    @DisplayName("测试1：基本 RAG 检索")
    public void testBasicRAG() {
        Assumptions.assumeTrue(chatModel != null, "需要设置 DEEPSEEK_API_KEY");
        
        System.out.println("\n===== 测试基本 RAG 检索 =====");
        
        // 1. 创建 QuestionAnswerAdvisor
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder()
            .vectorStore(vectorStore)
            .topK(2)
            .similarityThreshold(0.6)
            .build();
        
        // 2. 创建 ChatClient
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(qaAdvisor)
            .build();
        
        // 3. 提问
        String question = "Spring Boot 有什么特点？";
        System.out.println("问题: " + question);
        
        String answer = chatClient.prompt()
            .user(question)
            .call()
            .content();
        
        System.out.println("回答: " + answer);
        
        // 验证回答包含关键词
        Assertions.assertTrue(
            answer.contains("自动配置") || answer.contains("简化") || answer.contains("Spring Boot"),
            "回答应该包含 Spring Boot 相关内容"
        );
        
        System.out.println("✓ 基本 RAG 测试通过");
    }



    @Test
    @Order(2)
    @DisplayName("测试2：自定义 System Message 模板")
    public void testCustomTemplate() {
        Assumptions.assumeTrue(chatModel != null, "需要设置 DEEPSEEK_API_KEY");
        
        System.out.println("\n===== 测试自定义 System Message 模板 =====");
        
        String customTemplate = "你是一个技术专家。请基于以下参考资料回答问题：\n\n{question_answer_context}\n\n如果参考资料中没有相关信息，请明确告知用户。";
        
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder()
            .vectorStore(vectorStore)
            .userTextAdvise(customTemplate)
            .topK(3)
            .build();
        
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(qaAdvisor)
            .build();
        
        String question = "Spring Framework的主要功能是什么？";
        System.out.println("问题: " + question);
        
        String answer = chatClient.prompt()
            .user(question)
            .call()
            .content();
        
        System.out.println("回答: " + answer);
        
        Assertions.assertFalse(answer.isBlank(), "回答不应为空");
        
        System.out.println("✓ 自定义模板测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("测试3：无关问题处理")
    public void testIrrelevantQuestion() {
        Assumptions.assumeTrue(chatModel != null, "需要设置 DEEPSEEK_API_KEY");
        
        System.out.println("\n===== 测试无关问题处理 =====");
        
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder()
            .vectorStore(vectorStore)
            .topK(3)
            .similarityThreshold(0.7)
            .build();
        
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(qaAdvisor)
            .build();
        
        // 提问一个知识库中没有的问题
        String question = "Python 的主要特性是什么？";
        System.out.println("问题（知识库外）: " + question);
        
        String answer = chatClient.prompt()
            .user(question)
            .call()
            .content();
        
        System.out.println("回答: " + answer);
        
        // AI 应该能识别出知识库中没有相关信息
        // 但由于 AI 可能会基于自身知识回答，这里只验证有回答
        Assertions.assertFalse(answer.isBlank(), "应该有回答");
        
        System.out.println("✓ 无关问题测试通过");
    }


}
