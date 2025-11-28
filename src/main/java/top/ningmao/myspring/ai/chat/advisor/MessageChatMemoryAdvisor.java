package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.memory.ChatMemory;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;


/**
 * MessageChatMemoryAdvisor - 对话历史管理 Advisor
 * </p>
 * 功能：
 * 1. 在请求前：自动添加历史消息到 Prompt
 * 2. 在响应后：保存用户消息和助手响应到 ChatMemory
 *
 * @author 宁猫
 * @since 2025-11-28 14:59:48
 */
public class MessageChatMemoryAdvisor implements Advisor {

    private final ChatMemory chatMemory;
    private final String conversationId;
    
    // 使用 ThreadLocal 保存当前请求的用户消息
    private final ThreadLocal<List<Message>> currentUserMessages = new ThreadLocal<>();

    /**
     * 构造函数
     *
     * @param chatMemory ChatMemory 实例
     * @param conversationId 对话 ID
     */
    public MessageChatMemoryAdvisor(ChatMemory chatMemory, String conversationId) {
        this.chatMemory = chatMemory;
        this.conversationId = conversationId;
    }

    @Override
    public Prompt adviseRequest(Prompt prompt) {
        // 1. 获取历史消息
        List<Message> historyMessages = chatMemory.get(conversationId, -1);
        
        // 2. 获取当前请求的消息
        List<Message> currentMessages = prompt.getMessages();
        
        // 3. 保存当前用户消息到 ThreadLocal，供 adviseResponse 使用
        List<Message> userMessages = new ArrayList<>();
        for (Message message : currentMessages) {
            if (message instanceof UserMessage) {
                userMessages.add(message);
            }
        }
        currentUserMessages.set(userMessages);
        
        // 4. 合并历史消息和当前消息
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(historyMessages);
        allMessages.addAll(currentMessages);
        
        // 5. 创建新的 Prompt
        return new Prompt(allMessages, prompt.getOptions());
    }

    @Override
    public String adviseResponse(String response) {
        try {
            // 1. 获取当前用户消息
            List<Message> userMessages = currentUserMessages.get();
            if (userMessages == null || userMessages.isEmpty()) {
                return response;
            }
            
            // 2. 保存用户消息到 ChatMemory
            chatMemory.add(conversationId, userMessages);
            
            // 3. 保存助手响应到 ChatMemory
            chatMemory.add(conversationId, List.of(new AssistantMessage(response)));
            
            return response;
        } finally {
            // 4. 清理 ThreadLocal
            currentUserMessages.remove();
        }
    }

    /**
     * 获取 ConversationId
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * 清除对话历史
     */
    public void clear() {
        chatMemory.clear(conversationId);
    }

    /**
     * 获取对话历史
     */
    public List<Message> getHistory() {
        return chatMemory.get(conversationId, -1);
    }

    /**
     * Builder 模式
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ChatMemory chatMemory;
        private String conversationId;

        public Builder chatMemory(ChatMemory chatMemory) {
            this.chatMemory = chatMemory;
            return this;
        }

        public Builder conversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public MessageChatMemoryAdvisor build() {
            if (chatMemory == null) {
                throw new IllegalArgumentException("ChatMemory cannot be null");
            }
            if (conversationId == null || conversationId.isEmpty()) {
                throw new IllegalArgumentException("ConversationId cannot be null or empty");
            }
            return new MessageChatMemoryAdvisor(chatMemory, conversationId);
        }
    }
}
