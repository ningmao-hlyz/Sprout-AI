package top.ningmao.myspring.ai.chat.messages;

import java.util.Map;

/**
 * 用户消息
 * <p>
 * 代表用户发送的消息，最常用的消息类型
 *
 * @author 宁猫
 * @since 2025-11-25 17:37:10
 */
public class UserMessage extends AbstractMessage {
    public UserMessage(String content) {
        super(content);
    }

    public UserMessage(String content, Map<String, Object> metadata) {
        super(content, metadata);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.USER;
    }
}
