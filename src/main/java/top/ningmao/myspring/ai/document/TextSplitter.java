package top.ningmao.myspring.ai.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TextSplitter - 文本分块器抽象类
 * <p>
 * 核心功能：
 * - 实现 DocumentTransformer 接口
 * - 支持重叠（overlap）提高上下文连贯性
 * - 保留并传递元数据
 * - 子类实现具体分块策略
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public abstract class TextSplitter implements DocumentTransformer {

    /**
     * 每块的字符数
     */
    private final int chunkSize;

    /**
     * 块之间的重叠字符数
     */
    private final int chunkOverlap;

    /**
     * 默认块大小：500字符
     */
    public static final int DEFAULT_CHUNK_SIZE = 500;

    /**
     * 默认重叠：50字符
     */
    public static final int DEFAULT_CHUNK_OVERLAP = 50;

    /**
     * 默认构造函数
     */
    public TextSplitter() {
        this(DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_OVERLAP);
    }

    /**
     * 构造函数
     *
     * @param chunkSize 块大小
     */
    public TextSplitter(int chunkSize) {
        this(chunkSize, DEFAULT_CHUNK_OVERLAP);
    }

    /**
     * 完整构造函数
     *
     * @param chunkSize    块大小
     * @param chunkOverlap 重叠大小
     */
    public TextSplitter(int chunkSize, int chunkOverlap) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        if (chunkOverlap < 0) {
            throw new IllegalArgumentException("Chunk overlap cannot be negative");
        }
        if (chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException("Chunk overlap must be less than chunk size");
        }
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    /**
     * 实现 DocumentTransformer 接口
     * 转换文档列表（批量分块）
     */
    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> allChunks = new ArrayList<>();
        for (Document document : documents) {
            allChunks.addAll(split(document));
        }
        return allChunks;
    }

    /**
     * 分割单个文档
     *
     * @param document 原始文档
     * @return 分割后的文档列表
     */
    public List<Document> split(Document document) {
        // 调用抽象方法获取文本块
        List<String> textChunks = splitText(document.getContent());
        
        Map<String, Object> metadata = document.getMetadata();
        List<Document> chunks = new ArrayList<>();
        
        for (int i = 0; i < textChunks.size(); i++) {
            Document chunk = Document.builder()
                    .content(textChunks.get(i))
                    .metadata(metadata)
                    .metadata("chunk_index", i)
                    .metadata("original_doc_id", document.getId())
                    .build();
            chunks.add(chunk);
        }
        
        return chunks;
    }

    /**
     * 抽象方法：分割文本
     * 子类实现具体的分块策略
     *
     * @param text 原始文本
     * @return 文本块列表
     */
    protected abstract List<String> splitText(String text);

    /**
     * 获取块大小
     */
    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * 获取重叠大小
     */
    public int getChunkOverlap() {
        return chunkOverlap;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "chunkSize=" + chunkSize +
                ", chunkOverlap=" + chunkOverlap +
                '}';
    }
}
