package top.ningmao.myspring.ai.chat.messages;

/**
 * 消息类型枚举 - 定义对话中的角色
 *
 * @author 宁猫
 * @since 2025-11-25 16:26:43
 */
public enum MessageType {



    /**
     * 系统消息 - 用于设定 AI 的行为和上下文
     * 通常包含指令、角色设定、行为规范等
     */
    SYSTEM("system"),

    /**
     * 用户消息 - 用户的输入
     * 代表用户提出的问题或指令
     */
    USER("user"),

    /**
     * AI 助手消息 - AI 的回复
     * 代表 AI 模型生成的响应
     */
    ASSISTANT("assistant");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
