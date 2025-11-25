package top.ningmao.myspring.ai.chat.model;


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
    public String call(String message) {
        // 简单的固定回复
        return prefix + ": 收到你的消息 [" + message + "]";
    }
}
