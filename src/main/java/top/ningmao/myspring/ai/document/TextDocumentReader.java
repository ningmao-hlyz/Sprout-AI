package top.ningmao.myspring.ai.document;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TextDocumentReader - 文本文档读取器
 * 从文本文件或字符串加载文档
 * <p>
 * 支持：
 * - 文件路径读取
 * - 字符串直接读取
 * - 自定义元数据
 * - 字符编码指定
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class TextDocumentReader implements DocumentReader {

    /**
     * 文件路径（可选）
     */
    private final Path filePath;

    /**
     * 文本内容（可选）
     */
    private final String textContent;

    /**
     * 字符编码
     */
    private final Charset charset;

    /**
     * 元数据
     */
    private final Map<String, Object> metadata;

    /**
     * 构造函数 - 从文件路径读取
     *
     * @param filePath 文件路径
     */
    public TextDocumentReader(Path filePath) {
        this(filePath, StandardCharsets.UTF_8, new HashMap<>());
    }

    /**
     * 构造函数 - 从文件路径读取（指定编码）
     *
     * @param filePath 文件路径
     * @param charset  字符编码
     */
    public TextDocumentReader(Path filePath, Charset charset) {
        this(filePath, charset, new HashMap<>());
    }

    /**
     * 完整构造函数 - 从文件路径读取
     *
     * @param filePath 文件路径
     * @param charset  字符编码
     * @param metadata 元数据
     */
    public TextDocumentReader(Path filePath, Charset charset, Map<String, Object> metadata) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        this.filePath = filePath;
        this.textContent = null;
        this.charset = charset != null ? charset : StandardCharsets.UTF_8;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    /**
     * 构造函数 - 从字符串读取
     *
     * @param textContent 文本内容
     */
    public TextDocumentReader(String textContent) {
        this(textContent, new HashMap<>());
    }

    /**
     * 构造函数 - 从字符串读取（带元数据）
     *
     * @param textContent 文本内容
     * @param metadata    元数据
     */
    public TextDocumentReader(String textContent, Map<String, Object> metadata) {
        if (textContent == null) {
            throw new IllegalArgumentException("Text content cannot be null");
        }
        this.filePath = null;
        this.textContent = textContent;
        this.charset = StandardCharsets.UTF_8;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    @Override
    public List<Document> read() {
        try {
            String content = getContent();

            // 创建文档
            Document document = Document.builder()
                    .content(content)
                    .metadata(metadata)
                    .build();

            return Collections.singletonList(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read document", e);
        }
    }

    /**
     * 获取文档内容
     */
    private String getContent() throws Exception {
        if (textContent != null) {
            return textContent;
        }

        if (filePath != null) {
            // 读取文件内容
            byte[] bytes = Files.readAllBytes(filePath);
            String content = new String(bytes, charset);

            // 自动添加文件相关元数据
            metadata.putIfAbsent("source", filePath.toString());
            metadata.putIfAbsent("file_name", filePath.getFileName().toString());

            return content;
        }

        throw new IllegalStateException("No content source specified");
    }

    /**
     * Builder 模式
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path filePath;
        private String textContent;
        private Charset charset = StandardCharsets.UTF_8;
        private final Map<String, Object> metadata = new HashMap<>();

        public Builder filePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = Path.of(filePath);
            return this;
        }

        public Builder textContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            if (metadata != null) {
                this.metadata.putAll(metadata);
            }
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public TextDocumentReader build() {
            if (textContent != null) {
                return new TextDocumentReader(textContent, metadata);
            }
            if (filePath != null) {
                return new TextDocumentReader(filePath, charset, metadata);
            }
            throw new IllegalStateException("Either filePath or textContent must be specified");
        }
    }
}
