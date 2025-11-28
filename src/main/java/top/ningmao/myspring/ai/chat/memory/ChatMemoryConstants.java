package top.ningmao.myspring.ai.chat.memory;


/**
 * ChatMemory 常量
 *
 * @author 宁猫
 * @since 2025-11-28 15:24:44
 */
public final class ChatMemoryConstants {

    /**
     * 对话 ID 参数键
     * 用于在 Advisor 参数中传递 conversation ID
     */
    public static final String CONVERSATION_ID = "chat_memory_conversation_id";

    /**
     * 默认对话 ID
     */
    public static final String DEFAULT_CONVERSATION_ID = "default";

    private ChatMemoryConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
