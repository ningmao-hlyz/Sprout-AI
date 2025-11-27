package top.ningmao.myspring.ai.chat.messages;

import java.util.HashMap;
import java.util.Map;


/**
 * 工具消息
 * 用于表示工具调用的结果
 *
 * @author 宁猫
 * @since 2025-11-27 15:49:39
 */
public class ToolMessage implements Message {

    private final String toolCallId;
    private final String content;
    private final Map<String, Object> metadata;

    public ToolMessage(String toolCallId, String content) {
        this.toolCallId = toolCallId;
        this.content = content;
        this.metadata = new HashMap<>();
        this.metadata.put("tool_call_id", toolCallId);
    }

    public String getToolCallId() {
        return toolCallId;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TOOL;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "ToolMessage{" +
                "toolCallId='" + toolCallId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
