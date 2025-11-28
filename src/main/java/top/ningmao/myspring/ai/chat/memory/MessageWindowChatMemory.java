package top.ningmao.myspring.ai.chat.memory;

import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.MessageType;

import java.util.ArrayList;
import java.util.List;


/**
 * 消息窗口对话历史管理实现
 * 维护最近 N 条消息，保留系统消息
 *
 * @author 宁猫
 * @since 2025-11-28 13:59:59
 */
public class MessageWindowChatMemory implements ChatMemory {

    private final ChatMemoryRepository repository;
    private final int maxMessages;

    private MessageWindowChatMemory(Builder builder) {
        this.repository = builder.repository;
        this.maxMessages = builder.maxMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (conversationId == null || messages == null || messages.isEmpty()) {
            return;
        }

        // 添加新消息
        repository.add(conversationId, messages);

        // 获取所有消息
        List<Message> allMessages = repository.get(conversationId, -1);

        // 如果超过最大消息数，保留系统消息 + 最近的消息
        if (allMessages.size() > maxMessages) {
            // 分离系统消息和其他消息
            List<Message> systemMessages = new ArrayList<>();
            List<Message> otherMessages = new ArrayList<>();

            for (Message message : allMessages) {
                if (message.getMessageType() == MessageType.SYSTEM) {
                    systemMessages.add(message);
                } else {
                    otherMessages.add(message);
                }
            }

            // 计算需要保留的非系统消息数量
            int maxOtherMessages = maxMessages - systemMessages.size();
            if (maxOtherMessages < 0) {
                maxOtherMessages = 0;
            }

            // 获取最近的非系统消息
            List<Message> recentOtherMessages;
            if (otherMessages.size() > maxOtherMessages) {
                int startIndex = otherMessages.size() - maxOtherMessages;
                recentOtherMessages = otherMessages.subList(startIndex, otherMessages.size());
            } else {
                recentOtherMessages = otherMessages;
            }

            // 清除并重新添加：系统消息 + 最近的其他消息
            repository.clear(conversationId);
            repository.add(conversationId, systemMessages);
            repository.add(conversationId, recentOtherMessages);
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        return repository.get(conversationId, lastN);
    }

    @Override
    public void clear(String conversationId) {
        repository.clear(conversationId);
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private ChatMemoryRepository repository = new InMemoryChatMemoryRepository();
        private int maxMessages = 20;

        public Builder repository(ChatMemoryRepository repository) {
            this.repository = repository;
            return this;
        }

        public Builder maxMessages(int maxMessages) {
            this.maxMessages = maxMessages;
            return this;
        }

        public MessageWindowChatMemory build() {
            return new MessageWindowChatMemory(this);
        }
    }
}
