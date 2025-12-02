package top.ningmao.myspring.ai.document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SentenceTextSplitter - 按句子分块
 * <p>
 * 特点：
 * - 按句子边界切分（。！？.!?）
 * - 保持句子完整性
 * - 多个句子组成一个块，直到达到大小限制
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class SentenceTextSplitter extends TextSplitter {

    /**
     * 句子分隔符（中英文句号、问号、感叹号）
     */
    private static final Pattern SENTENCE_PATTERN = Pattern.compile(
            "[。！？.!?]+[\\s\\n]*"
    );

    /**
     * 构造函数
     */
    public SentenceTextSplitter() {
        super();
    }

    /**
     * 构造函数
     *
     * @param chunkSize 块大小
     */
    public SentenceTextSplitter(int chunkSize) {
        super(chunkSize);
    }

    /**
     * 完整构造函数
     *
     * @param chunkSize    块大小
     * @param chunkOverlap 重叠大小
     */
    public SentenceTextSplitter(int chunkSize, int chunkOverlap) {
        super(chunkSize, chunkOverlap);
    }

    @Override
    protected List<String> splitText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 按句子分割
        List<String> sentences = splitIntoSentences(text);
        
        // 2. 组合句子成块
        return combineSentences(sentences);
    }

    /**
     * 将文本分割成句子列表
     */
    private List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        Matcher matcher = SENTENCE_PATTERN.matcher(text);
        
        int lastEnd = 0;
        while (matcher.find()) {
            String sentence = text.substring(lastEnd, matcher.end()).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
            lastEnd = matcher.end();
        }
        
        // 添加最后一部分（如果有）
        if (lastEnd < text.length()) {
            String lastPart = text.substring(lastEnd).trim();
            if (!lastPart.isEmpty()) {
                sentences.add(lastPart);
            }
        }
        
        return sentences;
    }

    /**
     * 将句子组合成块
     */
    private List<String> combineSentences(List<String> sentences) {
        List<String> chunks = new ArrayList<>();
        
        if (sentences.isEmpty()) {
            return chunks;
        }
        
        int chunkSize = getChunkSize();
        int chunkOverlap = getChunkOverlap();
        
        StringBuilder currentChunk = new StringBuilder();
        List<String> currentSentences = new ArrayList<>();
        
        for (String sentence : sentences) {
            // 如果添加这个句子会超过限制
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                // 保存当前块
                chunks.add(currentChunk.toString());
                
                // 准备下一个块（考虑重叠）
                currentChunk = new StringBuilder();
                currentSentences = getOverlapSentences(currentSentences, chunkOverlap);
                
                // 添加重叠的句子
                for (String overlapSentence : currentSentences) {
                    currentChunk.append(overlapSentence).append(" ");
                }
                
                currentSentences.clear();
            }
            
            // 添加句子到当前块
            if (currentChunk.length() > 0 && !currentChunk.toString().endsWith(" ")) {
                currentChunk.append(" ");
            }
            currentChunk.append(sentence);
            currentSentences.add(sentence);
        }
        
        // 添加最后一个块
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }
        
        return chunks;
    }

    /**
     * 获取用于重叠的句子
     */
    private List<String> getOverlapSentences(List<String> sentences, int overlapSize) {
        List<String> overlap = new ArrayList<>();
        
        int totalLength = 0;
        // 从后往前选择句子，直到达到重叠大小
        for (int i = sentences.size() - 1; i >= 0; i--) {
            String sentence = sentences.get(i);
            if (totalLength + sentence.length() <= overlapSize) {
                overlap.add(0, sentence);  // 添加到开头
                totalLength += sentence.length();
            } else {
                break;
            }
        }
        
        return overlap;
    }
}
