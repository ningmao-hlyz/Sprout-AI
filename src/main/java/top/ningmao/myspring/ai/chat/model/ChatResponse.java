package top.ningmao.myspring.ai.chat.model;


import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.model.ModelResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatResponse - AI 模型的响应
 * 封装了 AI 模型返回的完整信息,不只是回答的text
 * </p>
 * <b>版本更新（参考 Spring AI）：</b>
 * - 新增 metadata 字段，支持存储额外信息
 * - 支持通过 metadata 存储完整的工具调用消息链
 *
 * @author 宁猫
 * @since 2025-11-25 18:53:11
 */
public class ChatResponse implements ModelResponse<Generation> {

    /** 用于存储完整消息链的 metadata key */
    public static final String METADATA_FULL_MESSAGE_CHAIN = "full_message_chain";

    private final List<Generation> generations;
    private final Map<String, Object> metadata;

    /**
     * 从 Generation 列表创建响应
     */
    public ChatResponse(List<Generation> generations) {
        this(generations, new HashMap<>());
    }

    /**
     * 从 Generation 列表和 metadata 创建响应
     * @since 2025-11-28
     */
    public ChatResponse(List<Generation> generations, Map<String, Object> metadata) {
        this.generations = generations != null ? new ArrayList<>(generations) : new ArrayList<>();
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    /**
     * 便捷构造函数 - 从文本创建
     */
    public ChatResponse(String text) {
        this.generations = new ArrayList<>();
        this.generations.add(new Generation(text));
        this.metadata = new HashMap<>();
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

    /**
     * 获取 metadata
     * @since 2025-11-28
     */
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    /**
     * 获取完整的消息链（如果存在）
     * 包含工具调用的所有中间消息
     * @return 完整的消息链，如果不存在则返回 null
     * @since 2025-11-28
     */
    @SuppressWarnings("unchecked")
    public List<Message> getFullMessageChain() {
        Object chain = metadata.get(METADATA_FULL_MESSAGE_CHAIN);
        if (chain instanceof List) {
            return new ArrayList<>((List<Message>) chain);
        }
        return null;
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
