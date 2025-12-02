package top.ningmao.myspring.ai.document;

import java.util.ArrayList;
import java.util.List;

/**
 * CharacterTextSplitter - 按固定字符数分块
 * <p>
 * 特点：
 * - 按固定字符数切分
 * - 支持重叠提高上下文连贯性
 * - 最简单直接的分块策略
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class CharacterTextSplitter extends TextSplitter {

    /**
     * 构造函数
     */
    public CharacterTextSplitter() {
        super();
    }

    /**
     * 构造函数
     *
     * @param chunkSize 块大小
     */
    public CharacterTextSplitter(int chunkSize) {
        super(chunkSize);
    }

    /**
     * 完整构造函数
     *
     * @param chunkSize    块大小
     * @param chunkOverlap 重叠大小
     */
    public CharacterTextSplitter(int chunkSize, int chunkOverlap) {
        super(chunkSize, chunkOverlap);
    }

    @Override
    protected List<String> splitText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;
        int chunkSize = getChunkSize();
        int chunkOverlap = getChunkOverlap();

        while (start < text.length()) {
            // 计算结束位置
            int end = Math.min(start + chunkSize, text.length());

            // 提取块内容
            String chunk = text.substring(start, end);
            chunks.add(chunk);

            // 移动到下一个块（考虑重叠）
            start += (chunkSize - chunkOverlap);

            // 防止无限循环
            if (chunkSize <= chunkOverlap) {
                break;
            }
        }

        return chunks;
    }
}
