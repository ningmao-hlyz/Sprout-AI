package top.ningmao.myspring.ai.chat.model;


import top.ningmao.myspring.ai.model.ModelResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatResponse - AI 模型的响应
 * 封装了 AI 模型返回的完整信息,不只是回答的text
 *
 * @author 宁猫
 * @since 2025-11-25 18:53:11
 */
public class ChatResponse implements ModelResponse<Generation> {


    private final List<Generation> generations;

    /**
     * 从 Generation 列表创建响应
     */
    public ChatResponse(List<Generation> generations) {
        this.generations = generations != null ? new ArrayList<>(generations) : new ArrayList<>();
    }

    /**
     * 便捷构造函数 - 从文本创建
     */
    public ChatResponse(String text) {
        this.generations = new ArrayList<>();
        this.generations.add(new Generation(text));
    }

    @Override
    public List<Generation> getResults() {
        return new ArrayList<>(generations);
    }

    /**
     * 快速获取输出内容文本
     */
    public String getOutput() {
        Generation result = getResult();
        if (result == null || result.getOutput() == null) {
            return null;
        }
        return result.getOutput().getContent();
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "generations=" + generations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatResponse that = (ChatResponse) o;
        return generations != null ? generations.equals(that.generations) : that.generations == null;
    }

    @Override
    public int hashCode() {
        return generations != null ? generations.hashCode() : 0;
    }
}
