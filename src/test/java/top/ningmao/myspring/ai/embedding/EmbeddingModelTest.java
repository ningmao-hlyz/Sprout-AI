package top.ningmao.myspring.ai.embedding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.huggingface.HuggingFaceEmbeddingModel;
import top.ningmao.myspring.ai.document.Document;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * EmbeddingModel 测试类
 * 
 * @author 宁猫
 * @since 2025-12-02
 */
public class EmbeddingModelTest {
    
    private EmbeddingModel embeddingModel;
    
    @BeforeEach
    public void setUp() {
        // 使用 HuggingFace Lightweight Embeddings API（免费，无需 API Key）
        // 默认使用 gte-multilingual-base 模型
        embeddingModel = new HuggingFaceEmbeddingModel();
        
        System.out.println("使用 HuggingFace Lightweight Embeddings API");
        System.out.println("模型: gte-multilingual-base");
    }
    
    /**
     * 测试1：单个文本嵌入
     */
    @Test
    public void testEmbedSingleText() {
        System.out.println("\n===== 测试1：单个文本嵌入 =====");
        
        String text = "Spring Framework 是一个企业级应用开发框架";
        float[] embedding = embeddingModel.embed(text);
        
        assertThat(embedding).isNotNull();
        assertThat(embedding.length).isGreaterThan(0);
        
        System.out.println("文本: " + text);
        System.out.println("向量维度: " + embedding.length);
        System.out.println("前5个值: ");
        for (int i = 0; i < Math.min(5, embedding.length); i++) {
            System.out.printf("  [%d] = %.6f%n", i, embedding[i]);
        }
        
        System.out.println(" 单个文本嵌入通过");
    }
    
    /**
     * 测试2：批量文本嵌入
     */
    @Test
    public void testEmbedMultipleTexts() {
        System.out.println("\n===== 测试2：批量文本嵌入 =====");
        
        List<String> texts = List.of(
                "Spring Framework 是一个企业级应用开发框架",
                "Spring Boot 简化了 Spring 应用的开发",
                "Spring Cloud 提供了微服务开发的工具集"
        );
        
        List<float[]> embeddings = embeddingModel.embed(texts);
        
        assertThat(embeddings).hasSize(3);
        for (float[] embedding : embeddings) {
            assertThat(embedding).isNotNull();
            assertThat(embedding.length).isGreaterThan(0);
        }
        
        System.out.println("嵌入文本数量: " + texts.size());
        System.out.println("返回向量数量: " + embeddings.size());
        System.out.println("向量维度: " + embeddings.get(0).length);
        
        System.out.println(" 批量文本嵌入通过");
    }
    
    /**
     * 测试3：Document 嵌入
     */
    @Test
    public void testEmbedDocument() {
        System.out.println("\n===== 测试3：Document 嵌入 =====");
        
        Document doc = Document.builder()
                .content("RAG 检索增强生成技术")
                .metadata("source", "技术文档")
                .build();
        
        float[] embedding = embeddingModel.embed(doc);
        
        assertThat(embedding).isNotNull();
        assertThat(embedding.length).isGreaterThan(0);
        
        System.out.println("文档内容: " + doc.getContent());
        System.out.println("向量维度: " + embedding.length);
        
        System.out.println(" Document 嵌入通过");
    }
    
    /**
     * 测试4：EmbeddingResponse（包含元数据）
     */
    @Test
    public void testEmbeddingResponse() {
        System.out.println("\n===== 测试4：EmbeddingResponse（包含元数据） =====");
        
        List<String> texts = List.of("测试文本1", "测试文本2");
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);

        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(2);
        assertThat(response.getMetadata()).isNotEmpty();
        
        System.out.println("结果数量: " + response.getResults().size());
        System.out.println("元数据: " + response.getMetadata());
        
        // 验证每个 Embedding
        for (Embedding embedding : response.getResults()) {
            assertThat(embedding.getEmbedding()).isNotNull();
            assertThat(embedding.getIndex()).isNotNull();
            assertThat(embedding.getDimension()).isGreaterThan(0);
        }
        
        System.out.println(" EmbeddingResponse 通过");
    }
    
    /**
     * 测试5：相似度计算
     */
    @Test
    public void testSimilarity() {
        System.out.println("\n===== 测试5：相似度计算 =====");
        
        String text1 = "Spring Framework 是一个 Java 框架";
        String text2 = "Spring 是 Java 开发框架";
        String text3 = "Python Django 是 Web 框架";
        
        float[] emb1 = embeddingModel.embed(text1);
        float[] emb2 = embeddingModel.embed(text2);
        float[] emb3 = embeddingModel.embed(text3);
        
        // 计算余弦相似度
        double sim12 = cosineSimilarity(emb1, emb2);
        double sim13 = cosineSimilarity(emb1, emb3);
        
        System.out.println("文本1: " + text1);
        System.out.println("文本2: " + text2);
        System.out.println("文本3: " + text3);
        System.out.printf("相似度(1-2): %.4f%n", sim12);
        System.out.printf("相似度(1-3): %.4f%n", sim13);
        
        // Spring 相关的文本应该比 Python 相关的更相似
        assertThat(sim12).isGreaterThan(sim13);
        
        System.out.println(" 相似度计算通过");
    }
    
    /**
     * 测试6：参数验证
     */
    @Test
    public void testValidation() {
        System.out.println("\n===== 测试6：参数验证 =====");
        
        // 文本不能为空
        assertThatThrownBy(() -> embeddingModel.embed((String) null))
                .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> embeddingModel.embed(""))
                .isInstanceOf(IllegalArgumentException.class);
        
        // Document 不能为 null
        assertThatThrownBy(() -> embeddingModel.embed((Document) null))
                .isInstanceOf(IllegalArgumentException.class);
        
        System.out.println(" 参数验证通过");
    }
    
    /**
     * 计算余弦相似度
     */
    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
