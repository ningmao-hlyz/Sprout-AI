package top.ningmao.myspring.ai.document;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 文档处理测试类
 * 测试 Document、TextDocumentReader、TextSplitter
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class DocumentProcessingTest {




    // ========== TextDocumentReader 测试 ==========

    /**
     * 测试1：文档读取功能
     * 包括：字符串读取、文件读取、元数据处理、Builder模式
     */
    @Test
    public void testDocumentReader(@TempDir Path tempDir) throws Exception {
        System.out.println("\n===== 测试1：文档读取功能 =====");

        // 1. 从字符串读取
        TextDocumentReader reader1 = new TextDocumentReader("这是一段测试文本内容");
        List<Document> docs1 = reader1.read();
        assertThat(docs1).hasSize(1);
        assertThat(docs1.get(0).getContent()).isEqualTo("这是一段测试文本内容");
        System.out.println(" 字符串读取通过");

        // 2. 带元数据读取
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("author", "宁猫");
        TextDocumentReader reader2 = new TextDocumentReader("教程内容", metadata);
        Document doc2 = reader2.read().get(0);
        assertThat(doc2.getMetadata("author")).isEqualTo("宁猫");
        System.out.println(" 元数据读取通过");

        // 3. 从临时文件读取
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "文件内容", StandardCharsets.UTF_8);
        TextDocumentReader reader3 = new TextDocumentReader(testFile);
        Document doc3 = reader3.read().get(0);
        assertThat(doc3.getMetadata("file_name")).isEqualTo("test.txt");
        System.out.println(" 临时文件读取通过");

        // 4. Builder 模式
        Path file2 = tempDir.resolve("builder.txt");
        Files.writeString(file2, "Builder测试", StandardCharsets.UTF_8);
        TextDocumentReader reader5 = TextDocumentReader.builder()
                .filePath(file2)
                .metadata("category", "测试")
                .build();
        assertThat(reader5.read().get(0).getMetadata("category")).isEqualTo("测试");
        System.out.println(" Builder 模式通过");
    }

    // ========== TextSplitter 测试 ==========

    /**
     * 测试2：文本分块功能
     * 包括：基本分块、参数验证、重叠验证、元数据传递
     */
    @Test
    public void testTextSplitting() {
        System.out.println("\n===== 测试2：基本文本分块 =====");

        // 创建一个较长的文档
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("这是第").append(i).append("句话。");
        }

        Document doc = new Document(sb.toString());

        // 使用字符分块器
        CharacterTextSplitter splitter = new CharacterTextSplitter(100, 20);
        List<Document> chunks = splitter.split(doc);

        System.out.println("原文档长度: " + doc.getContent().length());
        System.out.println("分块数量: " + chunks.size());

        assertThat(chunks).isNotEmpty();

        // 验证每个块
        for (int i = 0; i < chunks.size(); i++) {
            Document chunk = chunks.get(i);
            assertThat(chunk.getMetadata("chunk_index")).isEqualTo(i);
            assertThat(chunk.getMetadata("original_doc_id")).isEqualTo(doc.getId());

            System.out.println("块 " + i + " 长度: " + chunk.getContent().length());
        }

        System.out.println(" 基本分块通过");

        // 2. 参数验证
        assertThatThrownBy(() -> new CharacterTextSplitter(0, 10))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CharacterTextSplitter(100, -1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CharacterTextSplitter(100, 100))
                .isInstanceOf(IllegalArgumentException.class);
        System.out.println(" 参数验证通过");

        // 3. 重叠验证
        Document doc2 = new Document("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        CharacterTextSplitter splitter2 = new CharacterTextSplitter(10, 2);
        List<Document> chunks2 = splitter2.split(doc2);
        for (int i = 0; i < chunks2.size() - 1; i++) {
            String current = chunks2.get(i).getContent();
            String next = chunks2.get(i + 1).getContent();
            if (current.length() >= 2) {
                String overlap = current.substring(Math.max(0, current.length() - 2));
                assertThat(next).startsWith(overlap);
            }
        }
        System.out.println(" 重叠验证通过");

        // 4. 元数据传递
        Document doc3 = Document.builder()
                .content("A".repeat(500))
                .metadata("source", "测试来源")
                .build();
        CharacterTextSplitter splitter3 = new CharacterTextSplitter(100, 20);
        List<Document> chunks3 = splitter3.split(doc3);
        for (Document chunk : chunks3) {
            assertThat(chunk.getMetadata("source")).isEqualTo("测试来源");
            assertThat(chunk.getMetadata("original_doc_id")).isEqualTo(doc3.getId());
        }
        System.out.println(" 元数据传递通过");
    }

    /**
     * 测试3：多种分块策略
     * 包括：字符分块、段落分块、句子分块
     */
    @Test
    public void testMultipleSplitters() {
        System.out.println("\n===== 测试3：多种分块策略 =====");

        String content = "Spring Framework是开源框架。它提供依赖注入功能。" +
                "AOP是重要特性。\n\n" +
                "Spring Boot简化配置。它提供自动配置。";
        Document doc = new Document(content);

        // 1. 字符分块
        CharacterTextSplitter charSplitter = new CharacterTextSplitter(30, 5);
        List<Document> charChunks = charSplitter.split(doc);
        assertThat(charChunks).isNotEmpty();
        System.out.println("字符分块数: " + charChunks.size());

        // 2. 段落分块
        ParagraphTextSplitter paraSplitter = new ParagraphTextSplitter(100);
        List<Document> paraChunks = paraSplitter.split(doc);
        assertThat(paraChunks).isNotEmpty();
        System.out.println("段落分块数: " + paraChunks.size());

        // 3. 句子分块
        SentenceTextSplitter sentSplitter = new SentenceTextSplitter(50, 10);
        List<Document> sentChunks = sentSplitter.split(doc);
        assertThat(sentChunks).isNotEmpty();
        System.out.println("句子分块数: " + sentChunks.size());

        System.out.println(" 多种分块策略通过");
    }

    /**
     * 测试4：完整流程
     * 包括：文件读取 → 分块 → DocumentTransformer接口
     */
    @Test
    public void testCompleteFlow(@TempDir Path tempDir) throws Exception {
        System.out.println("\n===== 测试4：完整流程 =====");

        // 1. 创建测试文档
        Path file = tempDir.resolve("complete-test.txt");
        String content = "Spring Framework 简介\n\n" +
                "Spring Framework 是一个开源的 Java 企业级应用开发框架。" +
                "它提供了全面的基础设施支持，包括依赖注入、面向切面编程等核心功能。\n\n" +
                "Spring Boot 简化了 Spring 应用的开发。它提供了自动配置功能。";
        Files.writeString(file, content, StandardCharsets.UTF_8);

        // 2. 读取文档
        TextDocumentReader reader = TextDocumentReader.builder()
                .filePath(file)
                .metadata("category", "框架介绍")
                .build();

        List<Document> documents = reader.read();
        assertThat(documents).hasSize(1);

        // 3. 分块
        DocumentTransformer splitter = new CharacterTextSplitter(80, 20);
        List<Document> chunks = splitter.apply(documents);

        // 4. 验证结果
        assertThat(chunks).isNotEmpty();
        System.out.println("原文档数: " + documents.size());
        System.out.println("分块后文档数: " + chunks.size());

        // 验证元数据传递
        for (Document chunk : chunks) {
            assertThat(chunk.getMetadata("category")).isEqualTo("框架介绍");
            assertThat(chunk.getMetadata("chunk_index")).isNotNull();
        }

        System.out.println(" 完整流程测试通过");
    }
}
