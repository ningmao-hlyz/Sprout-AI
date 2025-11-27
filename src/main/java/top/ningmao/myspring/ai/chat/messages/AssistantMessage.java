package top.ningmao.myspring.ai.chat.messages;

import top.ningmao.myspring.ai.model.ToolCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 助手消息
 * 代表 AI 模型生成的响应消息
 *
 * @author 宁猫
 * @since 2025-11-25 17:39:24
 */
public class AssistantMessage extends AbstractMessage {

    private final List<ToolCall> toolCalls;

    public AssistantMessage(String content) {
        super(content);
        this.toolCalls = null;
    }

    public AssistantMessage(String content, Map<String, Object> metadata) {
        super(content, metadata);
        this.toolCalls = null;
    }

    /**
     * 带工具调用的构造函数
     *
     * @param content   消息内容
     * @param toolCalls 工具调用列表
     */
    public AssistantMessage(String content, List<ToolCall> toolCalls) {
        super(content);
        this.toolCalls = toolCalls;
        if (toolCalls != null && !toolCalls.isEmpty()) {
            Map<String, Object> metadata = new HashMap<>(getMetadata());
            metadata.put("tool_calls", toolCalls);
            this.metadata.putAll(metadata);
        }
    }

    /**
     * 获取工具调用列表
     *
     * @return 工具调用列表
     */
    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ASSISTANT;
    }
}
