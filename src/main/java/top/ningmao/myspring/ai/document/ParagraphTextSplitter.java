package top.ningmao.myspring.ai.document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ParagraphTextSplitter - 按段落分块
 * <p>
 * 特点：
 * - 按段落自然分割（双换行符）
 * - 如果段落过长，再按字符数切分
 * - 保持段落完整性
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class ParagraphTextSplitter extends TextSplitter {

    /**
     * 段落分隔符（双换行符或更多）
     */
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("\n\n+");

    /**
     * 构造函数
     */
    public ParagraphTextSplitter() {
        super();
    }

    /**
     * 构造函数
     *
     * @param chunkSize 块大小（段落超过此大小会被进一步切分）
     */
    public ParagraphTextSplitter(int chunkSize) {
        super(chunkSize);
    }

    /**
     * 完整构造函数
     *
     * @param chunkSize    块大小
     * @param chunkOverlap 重叠大小
     */
    public ParagraphTextSplitter(int chunkSize, int chunkOverlap) {
        super(chunkSize, chunkOverlap);
    }

    @Override
    protected List<String> splitText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        
        // 1. 按段落分割
        String[] paragraphs = PARAGRAPH_PATTERN.split(text);
        
        int chunkSize = getChunkSize();
        
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                continue;
            }
            
            // 2. 如果段落长度在限制内，直接添加
            if (paragraph.length() <= chunkSize) {
                result.add(paragraph);
            } else {
                // 3. 段落过长，按字符数切分
                List<String> subChunks = splitLongParagraph(paragraph);
                result.addAll(subChunks);
            }
        }
        
        return result;
    }

    /**
     * 切分过长的段落
     */
    private List<String> splitLongParagraph(String paragraph) {
        List<String> subChunks = new ArrayList<>();
        int start = 0;
        int chunkSize = getChunkSize();
        int chunkOverlap = getChunkOverlap();

        while (start < paragraph.length()) {
            int end = Math.min(start + chunkSize, paragraph.length());
            
            // 尝试在句子边界切分
            if (end < paragraph.length()) {
                // 寻找句号、问号、感叹号
                int sentenceBoundary = findSentenceBoundary(paragraph, start, end);
                if (sentenceBoundary > start) {
                    end = sentenceBoundary;
                }
            }
            
            subChunks.add(paragraph.substring(start, end));
            start += (chunkSize - chunkOverlap);
        }
        
        return subChunks;
    }

    /**
     * 查找句子边界
     */
    private int findSentenceBoundary(String text, int start, int end) {
        // 从 end 位置向前查找句子结束符
        for (int i = end - 1; i > start; i--) {
            char c = text.charAt(i);
            if (c == '。' || c == '！' || c == '？' || c == '.' || c == '!' || c == '?') {
                return i + 1;  // 包含结束符
            }
        }
        return end;  // 找不到就用原来的位置
    }
}
