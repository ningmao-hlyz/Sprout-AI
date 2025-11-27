package top.ningmao.myspring.ai.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.ai.model.function.Tool;
import top.ningmao.myspring.ai.model.function.ToolParam;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

/**
 * 简化版 Function Calling 测试
 * <p>
 * 演示新的简化 API：直接传入类而不需要手动构建 ToolCallback
 *
 * @author 宁猫
 * @since 2025-11-27
 */
public class SimplifiedFunctionCallingTest {

    private ClassPathXmlApplicationContext context;

    /**
     * 天气工具类
     */
    public static class WeatherTools {

        @Tool(description = "查询指定城市的天气情况")
        public String getWeather(
                @ToolParam(description = "城市名称") String city
        ) {
            System.out.println(" 工具调用：getWeather(" + city + ")");
            // 模拟天气查询
            return city + "今天晴天，温度3-12°C，空气质量良好";
        }

        @Tool(description = "查询指定城市未来几天的天气预报")
        public String getWeatherForecast(
                @ToolParam(description = "城市名称") String city,
                @ToolParam(description = "天数") int days
        ) {
            System.out.println(" 工具调用：getWeatherForecast(" + city + ", " + days + ")");
            return city + "未来" + days + "天天气：晴转多云";
        }
    }

    @BeforeEach
    public void setUp() {
        context = new ClassPathXmlApplicationContext("classpath:ai-config.xml");
    }

    /**
     * 测试新的简化 API - 直接传入类
     */
    @Test
    public void testSimplifiedAPI() {
        System.out.println("===== 简化 API 测试 =====\n");

        // 1. 获取 ChatModel
        ChatModel chatModel = context.getBean("chatModel", ChatModel.class);

        // 2. 使用新的简化 API - 直接传入类
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .tools(WeatherTools.class)
                .build();

        // 3. 调用
        Prompt prompt = new Prompt("北京今天天气怎么样？", options);
        System.out.println("用户问题：" + prompt.getMessages().get(0).getContent());
        System.out.println();

        String response = chatModel.call(prompt).getOutput();

        System.out.println("\nAI 回复：" + response);
        System.out.println("\n===== 测试完成 =====");
    }

}
