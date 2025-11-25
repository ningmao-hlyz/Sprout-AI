package top.ningmao.myspring.ai.chat.messages;

import java.util.Map;

/**
 * 系统消息
 * <p>
 * 用于设定 AI 的行为、角色和上下文
 * 通常包含指令、规则和背景信息
 *
 * @author 宁猫
 * @since 2025-11-25 17:38:31
 */
public class SystemMessage extends AbstractMessage {

    public SystemMessage(String content) {
        super(content);
    }

    public SystemMessage(String content, Map<String, Object> metadata) {
        super(content, metadata);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SYSTEM;
    }
}
