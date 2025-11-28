package top.ningmao.myspring.ai.chat.memory;

import top.ningmao.myspring.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 基于内存的对话历史存储实现
 * 使用 ConcurrentHashMap 存储消息
 *
 * @author 宁猫
 * @since 2025-11-28 14:00:17
 */
public class InMemoryChatMemoryRepository implements ChatMemoryRepository {

    private final ConcurrentHashMap<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (conversationId == null || messages == null || messages.isEmpty()) {
            return;
        }

        conversationHistory.compute(conversationId, (key, existingMessages) -> {
            if (existingMessages == null) {
                return new ArrayList<>(messages);
            }
            existingMessages.addAll(messages);
            return existingMessages;
        });
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null) {
            return new ArrayList<>();
        }

        List<Message> messages = conversationHistory.get(conversationId);
        if (messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }

        // 返回全部
        if (lastN < 0) {
            return new ArrayList<>(messages);
        }

        // 返回最近 N 条
        int size = messages.size();
        if (lastN >= size) {
            return new ArrayList<>(messages);
        }

        return new ArrayList<>(messages.subList(size - lastN, size));
    }

    @Override
    public void clear(String conversationId) {
        if (conversationId != null) {
            conversationHistory.remove(conversationId);
        }
    }
}
