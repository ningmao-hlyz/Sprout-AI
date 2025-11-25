package top.ningmao.myspring.ai.chat.prompt;

import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Prompt - 提示词请求
 *
 * @author 宁猫
 * @since 2025-11-25 18:25:06
 */
public class Prompt {

    /**
     * 消息列表
     */
    private final List<Message> messages;

    /**
     * 从单个字符串创建 Prompt（最简单的方式）
     */
    public Prompt(String content) {
        this(new UserMessage(content));
    }

    /**
     * 从单个消息创建 Prompt
     */
    public Prompt(Message message) {
        this.messages = new ArrayList<>();
        this.messages.add(message);
    }

    /**
     * 从消息列表创建 Prompt
     */
    public Prompt(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    /**
     * 从可变参数消息创建 Prompt
     */
    public Prompt(Message... messages) {
        this.messages = new ArrayList<>(Arrays.asList(messages));
    }

    /**
     * 获取指令
     */
    public List<Message> getInstructions() {
        return new ArrayList<>(messages);
    }

    /**
     * 获取消息列表
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    @Override
    public String toString() {
        return "Prompt{" +
                "messages=" + messages +
                '}';
    }
}
