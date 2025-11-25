package top.ningmao.myspring.ai.chat;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  Prompt 测试
 *
 * @author 宁猫
 * @since 2025-11-25 18:32:24
 */
public class PromptTest {

    @Test
    public void testPromptFromString() {
        // 最简单的创建方式
        Prompt prompt = new Prompt("你好，AI！");

        List<Message> messages = prompt.getMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("你好，AI！");

        System.out.println("Prompt: " + prompt);
    }

    @Test
    public void testPromptFromMessage() {
        // 从单个消息创建
        UserMessage userMessage = new UserMessage("告诉我关于 Spring 的知识");
        Prompt prompt = new Prompt(userMessage);

        assertThat(prompt.getMessages()).hasSize(1);
        assertThat(prompt.getMessages().get(0).getContent()).contains("Spring");

        System.out.println("Prompt: " + prompt);
    }

    @Test
    public void testPromptFromMultipleMessages() {
        // 从多个消息创建（带系统提示词）
        SystemMessage systemMessage = new SystemMessage("你是一个 Java 专家");
        UserMessage userMessage = new UserMessage("什么是 Spring IoC？");

        Prompt prompt = new Prompt(systemMessage, userMessage);

        List<Message> messages = prompt.getMessages();
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).contains("Java 专家");
        assertThat(messages.get(1).getContent()).contains("Spring IoC");

        System.out.println("Prompt with system message: " + prompt);
    }

}
