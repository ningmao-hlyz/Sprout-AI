package top.ningmao.myspring.ai.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.client.ChatClient;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * ChatClient 测试
 *
 * @author 宁猫
 * @since 2025-11-27 15:01:01
 */
public class ChatClientTest {

    private ClassPathXmlApplicationContext context;
    private ChatClient chatClient;

    @BeforeEach
    public void setUp() {
        context = new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        ChatModel chatModel = context.getBean("chatModel", ChatModel.class);
        chatClient = ChatClient.create(chatModel);
    }

    /**
     * 测试最简单的用法
     */
    @Test
    public void testSimplePrompt() {
        System.out.println("===== 简单提示词测试 =====\n");

        String response = chatClient.prompt()
                .user("Tell me a joke")
                .call()
                .content();

        System.out.println("AI 回复: " + response);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试便捷方法 prompt(String)
     */
    @Test
    public void testConvenienceMethod() {
        System.out.println("===== 便捷方法测试 =====\n");

        String response = chatClient.prompt("Tell me a fun fact about Java")
                .call()
                .content();

        System.out.println("AI 回复: " + response);

        assertThat(response).isNotNull();
        assertThat(response).contains("Java");

        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试模板参数
     */
    @Test
    public void testTemplateParameters() {
        System.out.println("===== 模板参数测试 =====\n");

        String response = chatClient.prompt()
                .user(u -> u.text("Tell me about {topic}")
                        .param("topic", "Spring Framework"))
                .call()
                .content();

        System.out.println("AI 回复: " + response);

        assertThat(response).isNotNull();
        assertThat(response).containsIgnoringCase("Spring");

        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试多个参数
     */
    @Test
    public void testMultipleParameters() {
        System.out.println("===== 多参数测试 =====\n");

        String response = chatClient.prompt()
                .user(u -> u.text("Tell me a {adjective} joke about {topic}")
                        .param("adjective", "funny")
                        .param("topic", "programming"))
                .call()
                .content();

        System.out.println("AI 回复: " + response);

        assertThat(response).isNotNull();

        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试系统消息
     */
    @Test
    public void testSystemMessage() {
        System.out.println("===== 系统消息测试 =====\n");

        String response = chatClient.prompt()
                .system("You are a helpful assistant that always responds in a professional tone.")
                .user("What is Spring AI?")
                .call()
                .content();

        System.out.println("AI 回复: " + response);

        assertThat(response).isNotNull();

        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试系统消息模板
     */
    @Test
    public void testSystemMessageWithTemplate() {
        System.out.println("===== 系统消息模板测试 =====\n");

        String response = chatClient.prompt()
                .system(s -> s.text("You are a {role} that helps with {task}.")
                        .param("role", "helpful assistant")
                        .param("task", "programming questions"))
                .user("Who are you?")
                .call()
                .content();

        System.out.println("AI 回复: " + response);

        assertThat(response).isNotNull();

        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试获取 ChatResponse
     */
    @Test
    public void testGetChatResponse() {
        System.out.println("===== ChatResponse 测试 =====\n");

        ChatResponse chatResponse = chatClient.prompt()
                .user("Tell me a joke")
                .call()
                .chatResponse();

        System.out.println("响应内容: " + chatResponse.getOutput());
        System.out.println("生成数: " + chatResponse.getResults().size());

        assertThat(chatResponse).isNotNull();
        assertThat(chatResponse.getResults()).isNotEmpty();

        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试默认系统消息
     */
    @Test
    public void testDefaultSystemMessage() {
        System.out.println("===== 默认系统消息测试 =====\n");

        ChatModel chatModel = context.getBean("chatModel", ChatModel.class);

        ChatClient customClient = ChatClient.builder(chatModel)
                .defaultSystem("You are a pirate. Always respond in pirate speak.")
                .defaultUser("Tell me a joke")
                .build();

        String response = customClient.prompt()
                .call()
                .content();

        System.out.println("AI 回复（海盗风格）: " + response);

        assertThat(response).isNotNull();

        System.out.println("\n===== 测试完成 =====");
    }
}
