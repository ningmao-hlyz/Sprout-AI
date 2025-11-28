package top.ningmao.myspring.ai.advisor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.advisor.MessageChatMemoryAdvisor;
import top.ningmao.myspring.ai.chat.memory.ChatMemory;
import top.ningmao.myspring.ai.chat.memory.ChatMemoryConstants;
import top.ningmao.myspring.ai.chat.memory.InMemoryChatMemoryRepository;
import top.ningmao.myspring.ai.chat.memory.MessageWindowChatMemory;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MessageChatMemoryAdvisor 测试
 *
 * @author 宁猫
 * @since 2025-11-28
 */
public class MessageChatMemoryAdvisorTest {

    private ChatMemory chatMemory;
    private MessageChatMemoryAdvisor advisor;
    private String conversationId;

    @BeforeEach
    public void setUp() {
        chatMemory = MessageWindowChatMemory.builder()
                .repository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();

        conversationId = "test-conversation-1";
        advisor = new MessageChatMemoryAdvisor(chatMemory);
    }
    
    private Map<String, Object> createParams(String conversationId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ChatMemoryConstants.CONVERSATION_ID, conversationId);
        return params;
    }

    @Test
    public void testBasicAdvisorFunctionality() {
        Map<String, Object> params = createParams(conversationId);
        
        // 1. 测试无历史的请求
        Prompt prompt1 = new Prompt("你好");
        Prompt advised1 = advisor.adviseRequest(prompt1, params);
        assertThat(advised1.getMessages()).hasSize(1);

        // 2. 保存响应到历史
        advisor.adviseResponse(prompt1, "你好！有什么可以帮助你的？", params);
        assertThat(advisor.getHistory(conversationId)).hasSize(2);

        // 3. 测试有历史的请求（自动包含历史）
        Prompt prompt2 = new Prompt("我想了解 Java");
        Prompt advised2 = advisor.adviseRequest(prompt2, params);
        assertThat(advised2.getMessages()).hasSize(3);  // 历史2条 + 当前1条

        // 4. 测试清除历史
        advisor.clear(conversationId);
        assertThat(advisor.getHistory(conversationId)).isEmpty();
    }

    @Test
    public void testMultiRoundConversation() {
        Map<String, Object> params = createParams(conversationId);
        
        // 添加系统消息
        chatMemory.add(conversationId, List.of(
                new SystemMessage("你是编程助手")
        ));

        // 第一轮
        Prompt prompt1 = new Prompt("什么是 Java？");
        advisor.adviseRequest(prompt1, params);
        advisor.adviseResponse(prompt1, "Java 是一门编程语言", params);

        // 第二轮（包含历史）
        Prompt prompt2 = new Prompt("它有什么特点？");
        Prompt advised2 = advisor.adviseRequest(prompt2, params);
        
        // 验证包含：系统消息 + 第一轮对话 + 当前消息
        assertThat(advised2.getMessages().size()).isGreaterThanOrEqualTo(4);
        assertThat(advised2.getMessages().get(0).getContent()).isEqualTo("你是编程助手");
    }
}
