package top.ningmao.myspring.ai.advisor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.advisor.MessageChatMemoryAdvisor;
import top.ningmao.myspring.ai.chat.client.ChatClient;
import top.ningmao.myspring.ai.chat.memory.ChatMemory;
import top.ningmao.myspring.ai.chat.memory.ChatMemoryConstants;
import top.ningmao.myspring.ai.chat.memory.InMemoryChatMemoryRepository;
import top.ningmao.myspring.ai.chat.memory.MessageWindowChatMemory;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.model.StreamingChatModel;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * ChatClient 与 Advisor 集成测试
 *
 * @author 宁猫
 * @since 2025-11-28 16:52:26
 */
public class ChatClientAdvisorIntegrationTest {

    private ChatMemory chatMemory;
    private ChatClient chatClient;

    @BeforeEach
    public void setUp() {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        StreamingChatModel chatModel = context.getBean("chatModel", StreamingChatModel.class);
        chatMemory = MessageWindowChatMemory.builder()
                .repository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();

        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @Test
    public void testBasicConversation() {
        String conversationId = "user-123";

        // 第一轮
        chatClient.prompt("你好，我叫宁猫")
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, conversationId))
                .call()
                .content();

        // 第二轮
        chatClient.prompt("你知道我叫什么吗")
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, conversationId))
                .call()
                .content();

        // 验证保存了两轮对话
        List<Message> history = chatMemory.get(conversationId, -1);
        System.out.println(history);
        assertThat(history).hasSize(4); // user + assistant + user + assistant
    }

    @Test
    public void testConversationIsolation() {
        // 两个不同的对话
        chatClient.prompt("你好，我是 宁猫")
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, "conv-1"))
                .call().content();

        chatClient.prompt("你知道我叫什么吗")
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, "conv-2"))
                .call().content();

        // 验证对话隔离
        List<Message> history1 = chatMemory.get("conv-1", -1);
        List<Message> history2 = chatMemory.get("conv-2", -1);
        System.out.println(history1);
        System.out.println(history2);

        assertThat(history1).hasSize(2);
        assertThat(history2).hasSize(2);
    }



}
