package top.ningmao.myspring.ai.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.client.ChatClient;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.model.function.Tool;
import top.ningmao.myspring.ai.model.function.ToolParam;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ChatClient Stream 流式输出测试
 *
 * @author 宁猫
 * @since 2025-11-27
 */
public class ChatClientStreamTest {

    private ClassPathXmlApplicationContext context;
    private ChatClient chatClient;

    @BeforeEach
    public void setUp() {
        context = new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        ChatModel chatModel = context.getBean("chatModel", ChatModel.class);
        chatClient = ChatClient.create(chatModel);
    }

    @Test
    public void testStreamContent() {
        StringBuilder fullResponse = new StringBuilder();

        chatClient.prompt()
                .user("介绍一下宁猫")
                .stream()
                .content(chunk -> {
                    System.out.print(chunk);
                    fullResponse.append(chunk);
                });

        assertThat(fullResponse.toString()).isNotEmpty();
    }

    public static class WeatherTools {
        @Tool(description = "查询指定城市的天气情况")
        public String getWeather(
                @ToolParam(description = "城市名称") String city
        ) {
            return city + " 今天晴天，温度 3-12°C，空气质量良好";
        }
    }

    @Test
    public void testStreamWithFunctionCalling() {
        StringBuilder fullResponse = new StringBuilder();

        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .tools(WeatherTools.class)
                .build();

        chatClient.prompt()
                .user("北京今天天气怎么样？")
                .options(options)
                .stream()
                .content(chunk -> {
                    System.out.print(chunk);
                    fullResponse.append(chunk);
                });

    }
}
