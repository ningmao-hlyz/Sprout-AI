package top.ningmao.myspring.ai.document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Document - AI 文档对象
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class Document {

    /**
     * 文档唯一标识符
     */
    private final String id;

    /**
     * 文档内容
     */
    private final String content;

    /**
     * 文档元数据（来源、分类、时间戳等）
     */
    private final Map<String, Object> metadata;

    /**
     * 构造函数 - 只提供内容
     *
     * @param content 文档内容
     */
    public Document(String content) {
        this(content, new HashMap<>());
    }

    /**
     * 构造函数 - 内容 + 元数据
     *
     * @param content  文档内容
     * @param metadata 元数据
     */
    public Document(String content, Map<String, Object> metadata) {
        this(generateId(), content, metadata);
    }

    /**
     * 完整构造函数
     *
     * @param id       文档ID
     * @param content  文档内容
     * @param metadata 元数据
     */
    public Document(String id, String content, Map<String, Object> metadata) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Document ID cannot be null or empty");
        }
        if (content == null) {
            throw new IllegalArgumentException("Document content cannot be null");
        }
        this.id = id;
        this.content = content;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    /**
     * 生成唯一 ID
     */
    private static String generateId() {
        return "doc-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
    }

    /**
     * 获取文档 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取文档内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 获取所有元数据（返回副本，保证不可变性）
     */
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    /**
     * 获取指定的元数据值
     *
     * @param key 元数据键
     * @return 元数据值，不存在返回 null
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Builder 模式
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String content;
        private final Map<String, Object> metadata = new HashMap<>();

        /**
         * 设置文档 ID
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * 设置文档内容
         */
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * 批量设置元数据
         */
        public Builder metadata(Map<String, Object> metadata) {
            if (metadata != null) {
                this.metadata.putAll(metadata);
            }
            return this;
        }

        /**
         * 添加单个元数据
         */
        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        /**
         * 构建 Document 对象
         */
        public Document build() {
            String docId = (id != null && !id.isBlank()) ? id : generateId();
            return new Document(docId, content, metadata);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String preview = content.length() > 50
                ? content.substring(0, 50) + "..."
                : content;
        return "Document{" +
                "id='" + id + '\'' +
                ", content='" + preview + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
