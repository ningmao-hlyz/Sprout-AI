package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.memory.ChatMemory;
import top.ningmao.myspring.ai.chat.memory.ChatMemoryConstants;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    private static final String DEFAULT_NAME = "MessageChatMemoryAdvisor";
    private static final int DEFAULT_ORDER = 0;

    private final ChatMemory chatMemory;
    private final String name;
    private final int order;
    
    // 使用 ThreadLocal 保存当前请求的上下文
    private final ThreadLocal<RequestContext> requestContext = new ThreadLocal<>();

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
        
        // 4. 保存当前用户消息到 ThreadLocal，供 adviseResponse 使用
        List<Message> userMessages = new ArrayList<>();
        for (Message message : currentMessages) {
            if (message instanceof UserMessage) {
                userMessages.add(message);
            }
        }
        
        // 5. 保存上下文
        requestContext.set(new RequestContext(conversationId, userMessages));
        
        // 6. 合并历史消息和当前消息
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(historyMessages);
        allMessages.addAll(currentMessages);
        
        // 7. 创建新的 Prompt
        return new Prompt(allMessages, prompt.getOptions());
    }

    @Override
    public String adviseResponse(Prompt prompt, String response, Map<String, Object> params) {
        try {
            // 1. 获取上下文
            RequestContext context = requestContext.get();
            if (context == null || context.userMessages.isEmpty()) {
                return response;
            }
            
            // 2. 保存用户消息到 ChatMemory
            chatMemory.add(context.conversationId, context.userMessages);
            
            // 3. 保存助手响应到 ChatMemory
            chatMemory.add(context.conversationId, List.of(new AssistantMessage(response)));
            
            return response;
        } finally {
            // 4. 清理 ThreadLocal
            requestContext.remove();
        }
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
     * 请求上下文（保存在 ThreadLocal 中）
     */
    private static class RequestContext {
        final String conversationId;
        final List<Message> userMessages;

        RequestContext(String conversationId, List<Message> userMessages) {
            this.conversationId = conversationId;
            this.userMessages = userMessages;
        }
    }
}
