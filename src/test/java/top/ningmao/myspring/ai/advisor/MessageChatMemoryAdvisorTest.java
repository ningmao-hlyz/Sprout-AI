package top.ningmao.myspring.ai.advisor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.advisor.MessageChatMemoryAdvisor;
import top.ningmao.myspring.ai.chat.memory.ChatMemory;
import top.ningmao.myspring.ai.chat.memory.InMemoryChatMemoryRepository;
import top.ningmao.myspring.ai.chat.memory.MessageWindowChatMemory;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.List;

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

        advisor = MessageChatMemoryAdvisor.builder()
                .chatMemory(chatMemory)
                .conversationId(conversationId)
                .build();
    }

    @Test
    public void testBasicAdvisorFunctionality() {
        // 1. 测试无历史的请求
        Prompt prompt1 = new Prompt("你好");
        Prompt advised1 = advisor.adviseRequest(prompt1);
        assertThat(advised1.getMessages()).hasSize(1);

        // 2. 保存响应到历史
        advisor.adviseResponse("你好！有什么可以帮助你的？");
        assertThat(advisor.getHistory()).hasSize(2);

        // 3. 测试有历史的请求（自动包含历史）
        Prompt prompt2 = new Prompt("我想了解 Java");
        Prompt advised2 = advisor.adviseRequest(prompt2);
        assertThat(advised2.getMessages()).hasSize(3);  // 历史2条 + 当前1条

        // 4. 测试清除历史
        advisor.clear();
        assertThat(advisor.getHistory()).isEmpty();
    }

    @Test
    public void testMultiRoundConversation() {
        // 添加系统消息
        chatMemory.add(conversationId, List.of(
                new SystemMessage("你是编程助手")
        ));

        // 第一轮
        advisor.adviseRequest(new Prompt("什么是 Java？"));
        advisor.adviseResponse("Java 是一门编程语言");

        // 第二轮（包含历史）
        Prompt prompt2 = new Prompt("它有什么特点？");
        Prompt advised2 = advisor.adviseRequest(prompt2);
        
        // 验证包含：系统消息 + 第一轮对话 + 当前消息
        assertThat(advised2.getMessages().size()).isGreaterThanOrEqualTo(4);
        assertThat(advised2.getMessages().get(0).getContent()).isEqualTo("你是编程助手");
    }
}
