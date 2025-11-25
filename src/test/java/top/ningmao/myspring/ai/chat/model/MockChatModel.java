package top.ningmao.myspring.ai.chat.model;


import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

/**
 * Mock ChatModel 实现 - 用于测试和演示
 *
 * @author 宁猫
 * @since 2025-11-25 15:29:23
 */
public class MockChatModel implements ChatModel {

    private final String prefix;

    public MockChatModel() {
        this.prefix = "NingMao AI";
    }

    public MockChatModel(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        // 获取所有消息并生成回复
        StringBuilder response = new StringBuilder(prefix + " 回复：\n");

        for (Message message : prompt.getMessages()) {
            response.append("- 收到 ").append(message.getMessageType().getValue())
                    .append(" 消息: [").append(message.getContent()).append("]\n");
        }

        // 创建响应
        return new ChatResponse(response.toString().trim());
    }
}
