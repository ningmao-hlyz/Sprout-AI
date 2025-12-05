package top.ningmao.myspring.ai.document;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MarkdownDocumentReader - Markdown 文档读取器
 * <p>
 * 功能：
 * 1. 读取 Markdown 文件或字符串
 * 2. 按标题层级分割文档（可选）
 * 3. 提取元数据（标题、代码块语言等）
 * 4. 保留 Markdown 结构信息
 * <p>
 * 支持的分割策略：
 * - NONE: 不分割，整个文档作为一个 Document
 * - BY_HEADING: 按一级标题分割
 * - BY_HEADING_LEVEL: 按指定标题层级分割
 *
 * @author 宁猫
 * @since 2025-12-04
 */
public class MarkdownDocumentReader implements DocumentReader {

    /**
     * 文件路径（可选）
     */
    private final Path filePath;

    /**
     * Markdown 内容（可选）
     */
    private final String markdownContent;

    /**
     * 字符编码
     */
    private final Charset charset;

    /**
     * 额外的元数据
     */
    private final Map<String, Object> additionalMetadata;

    /**
     * 分割策略
     */
    private final SplitStrategy splitStrategy;

    /**
     * 分割标题层级（1-6）
     */
    private final int splitHeadingLevel;

    /**
     * 是否包含代码块
     */
    private final boolean includeCodeBlock;

    /**
     * 是否自动进行文本分块（分割后再用 TextSplitter 切分）
     */
    private final boolean autoTextSplit;

    /**
     * 文本分块器（当 autoTextSplit=true 时使用）
     */
    private final TextSplitter textSplitter;

    /**
     * 分割策略枚举
     */
    public enum SplitStrategy {
        NONE,              // 不分割
        BY_HEADING,        // 按一级标题分割
        BY_HEADING_LEVEL   // 按指定标题层级分割
    }

    /**
     * 私有构造函数（使用 Builder）
     */
    private MarkdownDocumentReader(Builder builder) {
        this.filePath = builder.filePath;
        this.markdownContent = builder.markdownContent;
        this.charset = builder.charset;
        this.additionalMetadata = new HashMap<>(builder.additionalMetadata);
        this.splitStrategy = builder.splitStrategy;
        this.splitHeadingLevel = builder.splitHeadingLevel;
        this.includeCodeBlock = builder.includeCodeBlock;
        this.autoTextSplit = builder.autoTextSplit;
        this.textSplitter = builder.textSplitter;
    }

    @Override
    public List<Document> read() {
        try {
            String content = getContent();
            
            List<Document> documents;
            switch (splitStrategy) {
                case BY_HEADING:
                    documents = splitByHeading(content, 1);
                    break;
                case BY_HEADING_LEVEL:
                    documents = splitByHeading(content, splitHeadingLevel);
                    break;
                case NONE:
                default:
                    documents = Collections.singletonList(createDocument(content, new HashMap<>()));
                    break;
            }
            
            // 自动文本分块（如果启用）
            if (autoTextSplit && textSplitter != null) {
                return textSplitter.apply(documents);
            }
            
            return documents;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Markdown document", e);
        }
    }

    /**
     * 获取文档内容
     */
    private String getContent() throws Exception {
        if (markdownContent != null) {
            return markdownContent;
        }

        if (filePath != null) {
            byte[] bytes = Files.readAllBytes(filePath);
            return new String(bytes, charset);
        }

        throw new IllegalStateException("No content source specified");
    }

    /**
     * 按标题层级分割文档
     */
    private List<Document> splitByHeading(String content, int level) throws Exception {
        List<Document> documents = new ArrayList<>();
        
        // 正则匹配标题（如 ## Heading）
        String headingPattern = "^#{" + level + "}\\s+(.+)$";
        Pattern pattern = Pattern.compile(headingPattern, Pattern.MULTILINE);
        
        BufferedReader reader = new BufferedReader(new StringReader(content));
        StringBuilder currentSection = new StringBuilder();
        Map<String, Object> currentMetadata = new HashMap<>(additionalMetadata);
        
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            
            if (matcher.matches()) {
                // 遇到新的标题，保存前一个 section
                if (currentSection.length() > 0) {
                    documents.add(createDocument(currentSection.toString().trim(), currentMetadata));
                    currentSection = new StringBuilder();
                    currentMetadata = new HashMap<>(additionalMetadata);
                }
                
                // 提取新标题并保存到元数据
                String heading = matcher.group(1).trim();
                currentMetadata.put("heading", heading);
                currentMetadata.put("heading_level", level);
                
                // 将标题本身也加入内容
                currentSection.append(line).append("\n");
            } else {
                currentSection.append(line).append("\n");
            }
        }
        
        // 保存最后一个 section
        if (currentSection.length() > 0) {
            documents.add(createDocument(currentSection.toString().trim(), currentMetadata));
        }
        
        // 如果没有找到任何标题，返回整个文档
        if (documents.isEmpty()) {
            documents.add(createDocument(content, new HashMap<>(additionalMetadata)));
        }
        
        return documents;
    }

    /**
     * 创建 Document
     */
    private Document createDocument(String content, Map<String, Object> metadata) {
        // 提取代码块信息
        if (includeCodeBlock) {
            extractCodeBlockMetadata(content, metadata);
        }
        
        // 添加文件信息
        if (filePath != null) {
            metadata.putIfAbsent("source", filePath.toString());
            metadata.putIfAbsent("file_name", filePath.getFileName().toString());
            metadata.putIfAbsent("file_type", "markdown");
        }
        
        return Document.builder()
                .content(content)
                .metadata(metadata)
                .build();
    }

    /**
     * 提取代码块元数据
     */
    private void extractCodeBlockMetadata(String content, Map<String, Object> metadata) {
        // 匹配代码块：```language ... ```
        Pattern codeBlockPattern = Pattern.compile("```(\\w+)?\\s*\\n([\\s\\S]*?)```", Pattern.MULTILINE);
        Matcher matcher = codeBlockPattern.matcher(content);
        
        List<String> languages = new ArrayList<>();
        int codeBlockCount = 0;
        
        while (matcher.find()) {
            codeBlockCount++;
            String language = matcher.group(1);
            if (language != null && !language.isBlank()) {
                languages.add(language);
            }
        }
        
        if (codeBlockCount > 0) {
            metadata.put("code_block_count", codeBlockCount);
            metadata.put("has_code", true);
        }
        
        if (!languages.isEmpty()) {
            metadata.put("code_languages", languages);
        }
    }

    /**
     * Builder 模式
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path filePath;
        private String markdownContent;
        private Charset charset = StandardCharsets.UTF_8;
        private final Map<String, Object> additionalMetadata = new HashMap<>();
        private SplitStrategy splitStrategy = SplitStrategy.NONE;
        private int splitHeadingLevel = 1;
        private boolean includeCodeBlock = true;
        private boolean autoTextSplit = false;
        private TextSplitter textSplitter = null;

        /**
         * 设置文件路径
         */
        public Builder filePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        /**
         * 设置文件路径（字符串）
         */
        public Builder filePath(String filePath) {
            this.filePath = Path.of(filePath);
            return this;
        }

        /**
         * 设置 Markdown 内容
         */
        public Builder markdownContent(String markdownContent) {
            this.markdownContent = markdownContent;
            return this;
        }

        /**
         * 设置字符编码
         */
        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        /**
         * 添加元数据
         */
        public Builder metadata(Map<String, Object> metadata) {
            if (metadata != null) {
                this.additionalMetadata.putAll(metadata);
            }
            return this;
        }

        /**
         * 添加单个元数据
         */
        public Builder metadata(String key, Object value) {
            this.additionalMetadata.put(key, value);
            return this;
        }

        /**
         * 设置分割策略
         */
        public Builder splitStrategy(SplitStrategy splitStrategy) {
            this.splitStrategy = splitStrategy;
            return this;
        }

        /**
         * 按一级标题分割
         */
        public Builder splitByHeading() {
            this.splitStrategy = SplitStrategy.BY_HEADING;
            this.splitHeadingLevel = 1;
            return this;
        }

        /**
         * 按指定层级标题分割
         */
        public Builder splitByHeadingLevel(int level) {
            if (level < 1 || level > 6) {
                throw new IllegalArgumentException("Heading level must be between 1 and 6");
            }
            this.splitStrategy = SplitStrategy.BY_HEADING_LEVEL;
            this.splitHeadingLevel = level;
            return this;
        }

        /**
         * 不分割（默认）
         */
        public Builder noSplit() {
            this.splitStrategy = SplitStrategy.NONE;
            return this;
        }

        /**
         * 设置是否包含代码块元数据
         */
        public Builder includeCodeBlock(boolean includeCodeBlock) {
            this.includeCodeBlock = includeCodeBlock;
            return this;
        }

        /**
         * 启用自动文本分块
         * 在按标题分割后，自动使用 TextSplitter 进一步切分长章节
         */
        public Builder autoTextSplit(TextSplitter textSplitter) {
            this.autoTextSplit = true;
            this.textSplitter = textSplitter;
            return this;
        }

        /**
         * 构建 MarkdownDocumentReader
         */
        public MarkdownDocumentReader build() {
            if (markdownContent == null && filePath == null) {
                throw new IllegalStateException("Either markdownContent or filePath must be specified");
            }
            return new MarkdownDocumentReader(this);
        }
    }
}
