package top.ningmao.myspring.ai.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.client.ChatClient;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.mock.MockChatModel;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 多模型切换集成测试
 * 演示如何使用 ChatModelFactory 实现多模型动态切换
 * 包含模拟模型和真实的 DeepSeek 模型
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class MultiModelSwitchTest {

    private ChatModelFactory modelFactory;


    @BeforeEach
    public void setUp() {
        // 1. 创建工厂实例
        modelFactory = new ChatModelFactory();

        // 2. 注册模拟模型（用于测试，不需要 API）
        modelFactory.register("gpt-4", new MockChatModel(
                "gpt-4",
                "[GPT-4 模拟] 您的问题：{input}"
        ));

        modelFactory.register(new MockChatModel(
                "claude-3",
                "[Claude-3 模拟] 收到消息：{input}"
        ));

        modelFactory.register(new MockChatModel(
                "qwen-max",
                "[通义千问 模拟] 您问：{input}"
        ));

        // 3. 注册真实的 DeepSeek 模型
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:ai-config.xml");

        ChatModel chatModel = applicationContext.getBean("chatModel", ChatModel.class);
        modelFactory.register( chatModel);

    }

    /**
     * 测试1：基本的模型切换和对比
     */
    @Test
    public void testModelSwitchAndComparison() {
        System.out.println("\n===== 测试模型切换与对比 =====");

        String prompt = "解释什么是依赖注入";
        
        // 遍历所有模型，同一问题获得不同响应
        for (String modelName : modelFactory.getModelNames()) {
            System.out.println("\n【" + modelName + "】");
            ChatModel model = modelFactory.getModel(modelName);
            ChatClient client = ChatClient.create(model);
            String response = client.prompt(prompt).call().content();
            System.out.println("响应: " + response);
            
            // 验证响应不为空
            assertThat(response).isNotEmpty();
        }
    }

    /**
     * 测试2：流式输出（模拟模型 + 真实 DeepSeek）
     */
    @Test
    public void testStreamingOutput() {
        System.out.println("\n===== 测试流式输出 =====");

        // 模拟模型流式输出
        System.out.print("\n【GPT-4 模拟流式】 ");
        ChatModel gpt4 = modelFactory.getModel("gpt-4");
        ChatClient.create(gpt4)
                .prompt("讲个笑话")
                .stream()
                .content(chunk -> System.out.print(chunk));

        // DeepSeek 流式输出（真实或模拟）
        System.out.print("\n\n【DeepSeek 流式】 ");
        ChatModel deepseek = modelFactory.getModel("deepseek-chat");
        ChatClient.create(deepseek)
                .prompt("讲个笑话")
                .stream()
                .content(chunk -> System.out.print(chunk));

        System.out.println("\n");
    }

    /**
     * 测试3：动态模型选择策略
     */
    @Test
    public void testDynamicModelSelection() {
        System.out.println("\n===== 测试动态模型选择 =====");

        // 根据任务类型选择最合适的模型
        String taskType = "code";  // 编程任务
        String selectedModel = selectModelByTask(taskType);
        
        System.out.println("任务类型: " + taskType + " -> 选择模型: " + selectedModel);
        
        ChatModel model = modelFactory.getModel(selectedModel);
        String response = ChatClient.create(model)
                .prompt("写个快速排序算法")
                .call()
                .content();
        
        System.out.println("响应: " + response);
        assertThat(response).isNotEmpty();
    }

    /**
     * 根据任务类型选择模型（策略模式示例）
     */
    private String selectModelByTask(String taskType) {
        return switch(taskType) {
            case "code" -> "gpt-4";              // 编程任务
            case "analysis" -> "claude-3";       // 文本分析
            case "chinese" -> "deepseek-chat";   // 中文对话
            default -> "qwen-max";               // 通用问答
        };
    }
}
