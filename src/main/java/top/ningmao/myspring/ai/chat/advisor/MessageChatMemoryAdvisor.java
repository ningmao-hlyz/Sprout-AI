package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.memory.ChatMemory;
import top.ningmao.myspring.ai.chat.memory.ChatMemoryConstants;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.MessageType;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * MessageChatMemoryAdvisor - 对话历史管理 Advisor
 * </p>
 * 功能：
 * 1. 在请求前（adviseRequest）：获取历史消息 + 合并到 Prompt + 保存所有新消息（包括工具调用）
 * 2. 在响应后（adviseResponse）：保存 AI 最终响应
 * 3. 完整记录：支持 UserMessage、AssistantMessage、ToolMessage 等所有消息类型
 *
 * @author 宁猫
 * @since 2025-11-28 14:59:48
 */
public class MessageChatMemoryAdvisor implements Advisor {

    private static final String DEFAULT_NAME = "MessageChatMemoryAdvisor";
    private static final int DEFAULT_ORDER = 0;

    private final ChatMemory chatMemory;
    private final String name;
    private final int order;

    /**
     * 构造函数
     *
     * @param chatMemory ChatMemory 实例
     */
    public MessageChatMemoryAdvisor(ChatMemory chatMemory) {
        this(chatMemory, DEFAULT_NAME, DEFAULT_ORDER);
    }

    /**
     * 构造函数
     *
     * @param chatMemory ChatMemory 实例
     * @param name       Advisor 名称
     * @param order      执行顺序
     */
    public MessageChatMemoryAdvisor(ChatMemory chatMemory, String name, int order) {
        this.chatMemory = chatMemory;
        this.name = name;
        this.order = order;
    }

    @Override
    public Prompt adviseRequest(Prompt prompt, Map<String, Object> params) {
        // 1. 从参数获取 conversation ID
        String conversationId = getConversationId(params);
        
        // 2. 获取历史消息
        List<Message> historyMessages = chatMemory.get(conversationId, -1);
        
        // 3. 获取当前请求的消息
        List<Message> currentMessages = prompt.getMessages();
        
        // 4. 保存所有新消息到历史（除了 SystemMessage）
        List<Message> newMessages = new ArrayList<>();
        for (Message message : currentMessages) {
            // 过滤掉 SystemMessage（系统消息不保存到历史）
            if (!(message instanceof SystemMessage)) {
                // 检查这条消息是否已在历史中（避免重复保存）
                if (!isMessageInHistory(message, historyMessages)) {
                    newMessages.add(message);
                }
            }
        }
        if (!newMessages.isEmpty()) {
            chatMemory.add(conversationId, newMessages);
        }
        
        // 5. 合并历史消息和当前消息
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(historyMessages);
        allMessages.addAll(currentMessages);
        
        // 6. 创建新的 Prompt
        return new Prompt(allMessages, prompt.getOptions());
    }

    @Override
    public String adviseResponse(Prompt prompt, String response, Map<String, Object> params) {
        // 1. 从参数获取 conversation ID
        String conversationId = getConversationId(params);
        
        // 2. 只保存 AI 响应（用户消息已在 adviseRequest 中保存）
        if (response != null && !response.isEmpty()) {
            chatMemory.add(conversationId, List.of(new AssistantMessage(response)));
        }
        
        return response;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * 从参数中获取 conversation ID
     */
    private String getConversationId(Map<String, Object> params) {
        if (params == null) {
            return ChatMemoryConstants.DEFAULT_CONVERSATION_ID;
        }
        Object id = params.get(ChatMemoryConstants.CONVERSATION_ID);
        return id != null ? id.toString() : ChatMemoryConstants.DEFAULT_CONVERSATION_ID;
    }

    /**
     * 清除对话历史
     */
    public void clear(String conversationId) {
        chatMemory.clear(conversationId);
    }

    /**
     * 获取对话历史
     */
    public List<Message> getHistory(String conversationId) {
        return chatMemory.get(conversationId, -1);
    }

    /**
     * 检查消息是否已存在于历史中（避免重复保存）
     * 通过内容和类型进行简单判断
     */
    private boolean isMessageInHistory(Message message, List<Message> historyMessages) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return false;
        }
        
        String content = message.getContent();
        MessageType type = message.getMessageType();
        
        // 简单判断：如果历史中有相同类型和内容的消息，认为已存在
        for (Message historyMessage : historyMessages) {
            if (historyMessage.getMessageType() == type && 
                historyMessage.getContent().equals(content)) {
                return true;
            }
        }
        
        return false;
    }

}
