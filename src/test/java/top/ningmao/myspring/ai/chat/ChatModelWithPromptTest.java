package top.ningmao.myspring.ai.chat;

import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.model.MockChatModel;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试 ChatModel 的 Prompt 调用方式
 *
 * @author 宁猫
 * @since 2025-11-25 19:04:17
 */
public class ChatModelWithPromptTest {

    @Test
    public void testCallWithPrompt() {
        ChatModel chatModel = new MockChatModel();

        // 使用 Prompt 调用
        Prompt prompt = new Prompt("你好，Sprout AI！");
        ChatResponse response = chatModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getOutput()).contains("你好，Sprout AI！");

        System.out.println("Response: " + response.getOutput());
    }

    @Test
    public void testCallWithString() {
        ChatModel chatModel = new MockChatModel();

        // 使用简化的 String 调用（默认方法）
        String response = chatModel.call("测试简化调用");

        assertThat(response).isNotNull();
        assertThat(response).contains("测试简化调用");

        System.out.println("Response: " + response);
    }

    @Test
    public void testCallWithMultipleMessages() {
        ChatModel chatModel = new MockChatModel("智能助手");

        // 创建带系统提示词的 Prompt
        Prompt prompt = new Prompt(
                new SystemMessage("你是一个 Java 专家"),
                new UserMessage("什么是 Spring IoC？")
        );

        ChatResponse response = chatModel.call(prompt);

        assertThat(response.getOutput()).contains("Java 专家");
        assertThat(response.getOutput()).contains("Spring IoC");

        System.out.println("Multi-message response:\n" + response.getOutput());
    }

    @Test
    public void testChatResponseGeneration() {
        ChatModel chatModel = new MockChatModel();
        Prompt prompt = new Prompt("测试");

        ChatResponse response = chatModel.call(prompt);

        // 测试 Generation
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isNotNull();
        assertThat(response.getResults()).hasSize(1);

        System.out.println("Generation: " + response.getResult());
    }
}
