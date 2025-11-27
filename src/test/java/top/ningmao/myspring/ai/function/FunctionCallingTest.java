package top.ningmao.myspring.ai.function;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.ai.model.function.MethodToolCallback;
import top.ningmao.myspring.ai.model.function.Tool;
import top.ningmao.myspring.ai.model.function.ToolCallback;
import top.ningmao.myspring.ai.model.function.ToolParam;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Function Calling 测试
 * 演示如何使用 @Tool 注解定义工具，并让 AI 自动调用
 *
 * @author 宁猫
 * @since 2025-11-26
 */
public class FunctionCallingTest {

    /**
     * 工具类 - 天气查询
     */
    public static class WeatherTools {
        
        @Tool(description = "查询指定城市的天气情况，返回天气和温度")
        public String getWeather(
            @ToolParam(description = "城市名称，例如：北京、上海") String city
        ) {
            // 模拟天气查询
            return "根据最新气象数据，" + city + "今天晴天，温度3-12°C，空气质量良好";
        }
    }

    /**
     * 工具类 - 计算器
     */
    public static class CalculatorTools {
        
        @Tool(description = "执行数学计算，支持加减乘除")
        public String calculate(
            @ToolParam(description = "数学表达式，例如：1+2*3") String expression
        ) {
            // 简单计算
            try {
                if (expression.contains("*")) {
                    String[] parts = expression.split("\\*");
                    int result = Integer.parseInt(parts[0].trim()) * Integer.parseInt(parts[1].trim());
                    return "计算结果：" + expression + " = " + result;
                }
                return "计算结果：" + expression + " = 0";
            } catch (Exception e) {
                return "计算出错：" + e.getMessage();
            }
        }
    }

    /**
     * 测试基本的 Function Calling
     */
    @Test
    public void testBasicFunctionCalling() throws Exception {
        System.out.println("===== Function Calling 基本测试 =====\n");

        // 1. 获取 ChatModel
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        ChatModel chatModel = context.getBean("chatModel", ChatModel.class);

        // 2. 创建工具
        WeatherTools weatherTools = new WeatherTools();
        Method getWeatherMethod = WeatherTools.class.getMethod("getWeather", String.class);
        ToolCallback weatherTool = new MethodToolCallback(weatherTools, getWeatherMethod);

        // 3. 配置工具
        List<ToolCallback> tools = new ArrayList<>();
        tools.add(weatherTool);

        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .tools(tools)
                .build();

        // 4. 询问需要使用工具的问题
        System.out.println("用户问题：北京今天天气怎么样？\n");
        
        Prompt prompt = new Prompt("北京今天天气怎么样？", options);
        String response = chatModel.call(prompt).getOutput();

        System.out.println("AI 回复：" + response);
        System.out.println("\n===== 测试完成 =====");
    }

    /**
     * 测试多个工具
     */
    @Test
    public void testMultipleTools() throws Exception {
        System.out.println("===== 多工具测试 =====\n");

        // 1. 获取 ChatModel
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        ChatModel chatModel = context.getBean("chatModel", ChatModel.class);

        // 2. 创建多个工具
        WeatherTools weatherTools = new WeatherTools();
        CalculatorTools calculatorTools = new CalculatorTools();

        Method getWeatherMethod = WeatherTools.class.getMethod("getWeather", String.class);
        Method calculateMethod = CalculatorTools.class.getMethod("calculate", String.class);

        List<ToolCallback> tools = new ArrayList<>();
        tools.add(new MethodToolCallback(weatherTools, getWeatherMethod));
        tools.add(new MethodToolCallback(calculatorTools, calculateMethod));

        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .tools(tools)
                .build();

        // 3. 询问需要计算的问题
        System.out.println("用户问题：3821乘21等于多少？\n");

        Prompt prompt = new Prompt("3821乘21等于多少？上海的天气怎么样,344除22呢", options);
        String response = chatModel.call(prompt).getOutput();

        System.out.println("AI 回复：" + response);
        System.out.println("\n===== 测试完成 =====");
    }


}
