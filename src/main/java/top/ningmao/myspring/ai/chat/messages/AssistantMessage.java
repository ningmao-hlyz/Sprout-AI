package top.ningmao.myspring.ai.chat.messages;

import java.util.Map;

/**
 * AI 助手消息
 * <p>
 * 代表 AI 模型生成的响应消息
 *
 * @author 宁猫
 * @since 2025-11-25 17:39:24
 */
public class AssistantMessage extends AbstractMessage {

    public AssistantMessage(String content) {
        super(content);
    }

    public AssistantMessage(String content, Map<String, Object> metadata) {
        super(content, metadata);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ASSISTANT;
    }
}
