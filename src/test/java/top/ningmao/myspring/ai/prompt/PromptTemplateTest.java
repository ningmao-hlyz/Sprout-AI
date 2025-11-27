package top.ningmao.myspring.ai.prompt;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.ai.chat.prompt.template.DefaultTemplateRenderer;
import top.ningmao.myspring.ai.chat.prompt.template.PromptTemplate;
import top.ningmao.myspring.ai.chat.prompt.template.SystemPromptTemplate;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Prompt 模版化测试
 *
 * @author 宁猫
 * @since 2025-11-27 14:29:08
 */
public class PromptTemplateTest {

    /**
     * 测试基本的占位符替换
     */
    @Test
    public void testBasicPlaceholderReplacement() {
        System.out.println("===== 基本占位符替换测试 =====\n");

        PromptTemplate template = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        String result = template.render(Map.of(
                "adjective", "funny",
                "topic", "programming"
        ));

        System.out.println("模板: " + template.getTemplate());
        System.out.println("结果: " + result);

        assertThat(result).isEqualTo("Tell me a funny joke about programming");

    }

    /**
     * 测试 Builder 模式
     */
    @Test
    public void testBuilderPattern() {
        System.out.println("===== Builder 模式测试 =====\n");

        PromptTemplate template = PromptTemplate.builder()
                .template("Tell me about {topic}")
                .build();

        String result = template.render(Map.of("topic", "Spring Framework"));

        System.out.println("结果: " + result);
        assertThat(result).contains("Spring Framework");

    }

    /**
     * 测试自定义分隔符
     */
    @Test
    public void testCustomDelimiters() {
        System.out.println("===== 自定义分隔符测试 =====\n");

        PromptTemplate template = PromptTemplate.builder()
                .renderer(DefaultTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .template("Tell me the names of 5 movies whose soundtrack was composed by <composer>.")
                .build();

        String result = template.render(Map.of("composer", "John Williams"));

        System.out.println("模板: " + template.getTemplate());
        System.out.println("结果: " + result);

        assertThat(result).contains("John Williams");

    }

    /**
     * 测试 createMessage 方法
     */
    @Test
    public void testCreateMessage() {
        System.out.println("===== CreateMessage 测试 =====\n");

        PromptTemplate template = new PromptTemplate("Hello, {name}!");
        Message message = template.createMessage(Map.of("name", "World"));

        System.out.println("消息类型: " + message.getMessageType());
        System.out.println("消息内容: " + message.getContent());

        assertThat(message).isInstanceOf(UserMessage.class);
        assertThat(message.getContent()).isEqualTo("Hello, World!");

    }

    /**
     * 测试 create Prompt 方法
     */
    @Test
    public void testCreatePrompt() {
        System.out.println("===== Create Prompt 测试 =====\n");

        PromptTemplate template = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = template.create(Map.of(
                "adjective", "funny",
                "topic", "Java"
        ));

        System.out.println("Prompt 消息数: " + prompt.getMessages().size());
        System.out.println("Prompt 内容: " + prompt.getMessages().get(0).getContent());

        assertThat(prompt.getMessages()).hasSize(1);
        assertThat(prompt.getMessages().get(0).getContent()).contains("funny");
        assertThat(prompt.getMessages().get(0).getContent()).contains("Java");

    }

    /**
     * 测试 SystemPromptTemplate
     */
    @Test
    public void testSystemPromptTemplate() {
        System.out.println("===== SystemPromptTemplate 测试 =====\n");

        String systemText = """
                You are a helpful AI assistant that helps people find information.
                Your name is {name}
                You should reply to the user's request with your name and also in the style of a {voice}.
                """;

        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemTemplate.createMessage(Map.of(
                "name", "Sprout AI",
                "voice", "friendly and professional"
        ));

        System.out.println("消息类型: " + systemMessage.getMessageType());
        System.out.println("消息内容:\n" + systemMessage.getContent());

        assertThat(systemMessage).isInstanceOf(SystemMessage.class);
        assertThat(systemMessage.getContent()).contains("Sprout AI");
        assertThat(systemMessage.getContent()).contains("friendly and professional");

        System.out.println("\n===== 测试完成 =====");
    }



    /**
     * 测试与 ChatModel 集成 - 完整流程
     */
    @Test
    public void testIntegrationWithChatModel() {
        System.out.println("===== ChatModel 集成测试 =====\n");

        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        ChatModel chatModel = context.getBean("chatModel", ChatModel.class);

        // 使用 PromptTemplate
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of(
                "adjective", "funny",
                "topic", "programming"
        ));

        System.out.println("发送提示词: " + prompt.getMessages().get(0).getContent());
        System.out.println();

        String response = chatModel.call(prompt).getOutput();

        System.out.println("AI 回复: " + response);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

    }
}
