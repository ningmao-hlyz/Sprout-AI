package top.ningmao.myspring.ai.chat.prompt;

import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.model.ModelRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Prompt - 提示词请求
 *
 * @author 宁猫
 * @since 2025-11-25 18:25:06
 */
public class Prompt implements ModelRequest<List<Message>> {

    private final List<Message> messages;
    private final ChatOptions chatOptions;

    /**
     * 从单个字符串创建 Prompt
     */
    public Prompt(String content) {
        this(new UserMessage(content), null);
    }

    /**
     * 从字符串和配置创建 Prompt
     */
    public Prompt(String content, ChatOptions chatOptions) {
        this(new UserMessage(content), chatOptions);
    }

    /**
     * 从单个消息创建 Prompt
     */
    public Prompt(Message message) {
        this(message, null);
    }

    /**
     * 从单个消息和配置创建 Prompt
     */
    public Prompt(Message message, ChatOptions chatOptions) {
        this.messages = new ArrayList<>();
        this.messages.add(message);
        this.chatOptions = chatOptions;
    }

    /**
     * 从消息列表创建 Prompt
     */
    public Prompt(List<Message> messages) {
        this(messages, null);
    }

    /**
     * 从消息列表和配置创建 Prompt
     */
    public Prompt(List<Message> messages, ChatOptions chatOptions) {
        this.messages = new ArrayList<>(messages);
        this.chatOptions = chatOptions;
    }

    /**
     * 从可变参数消息创建 Prompt
     */
    public Prompt(Message... messages) {
        this(Arrays.asList(messages), null);
    }

    @Override
    public List<Message> getInstructions() {
        return new ArrayList<>(messages);
    }

    @Override
    public ChatOptions getOptions() {
        return chatOptions;
    }

    /**
     * 获取消息列表
     */
    public List<Message> getMessages() {
        return getInstructions();
    }

    @Override
    public String toString() {
        return "Prompt{" +
                "messages=" + messages +
                ", chatOptions=" + chatOptions +
                '}';
    }
}
