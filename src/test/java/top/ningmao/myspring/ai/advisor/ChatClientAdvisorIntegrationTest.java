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
import top.ningmao.myspring.ai.chat.messages.MessageType;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.model.StreamingChatModel;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.model.function.ToolCallback;
import top.ningmao.myspring.ai.model.function.ToolDefinition;
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
        chatClient.prompt("Alice 说：你好")
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, "conv-1"))
                .call().content();

        chatClient.prompt("Bob 说：你好")
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, "conv-2"))
                .call().content();

        // 验证对话隔离
        List<Message> history1 = chatMemory.get("conv-1", -1);
        List<Message> history2 = chatMemory.get("conv-2", -1);

        assertThat(history1).hasSize(2);
        assertThat(history2).hasSize(2);
        assertThat(history1.get(0).getContent()).contains("Alice");
        assertThat(history2.get(0).getContent()).contains("Bob");
    }

    @Test
    public void testToolCallWithMessageChain() {
        // 创建天气查询工具
        ToolCallback weatherTool = createWeatherTool();

        // 配置带工具的 ChatOptions
        DeepSeekChatOptions options = new DeepSeekChatOptions();
        options.setTools(List.of(weatherTool));

        String conversationId = "tool-test";

        // 调用工具
        System.out.println("\n===== 测试工具调用 + 完整消息链保存 =====");
        ChatResponse response = chatClient.prompt("北京今天天气怎么样")
                .options(options)
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();

        System.out.println("AI 响应: " + response.getOutput());

        // 验证完整消息链
        List<Message> fullChain = response.getFullMessageChain();
        System.out.println("\n完整工具调用消息链：");
        if (fullChain != null) {
            for (int i = 0; i < fullChain.size(); i++) {
                Message msg = fullChain.get(i);
                String preview = msg.getContent().length() > 60 ? 
                    msg.getContent().substring(0, 60) + "..." : 
                    msg.getContent();
                System.out.println((i + 1) + ". " + msg.getMessageType() + ": " + preview);
            }
        }

        // 验证历史记录
        List<Message> history = chatMemory.get(conversationId, -1);
        System.out.println("\nChatMemory 保存的历史消息：");
        for (int i = 0; i < history.size(); i++) {
            Message msg = history.get(i);
            System.out.println((i + 1) + ". " + msg.getMessageType() + ": " + 
                (msg.getContent().length() > 50 ? msg.getContent().substring(0, 50) + "..." : msg.getContent()));
        }

        // 断言：应该包含完整的工具调用链
        // UserMessage + AssistantMessage(tool_calls) + ToolMessage + AssistantMessage(final)
        assertThat(history.size()).isGreaterThanOrEqualTo(3); // 至少 3 条
        
        // 验证消息类型
        long userMsgCount = history.stream().filter(m -> m.getMessageType() == MessageType.USER).count();
        long assistantMsgCount = history.stream().filter(m -> m.getMessageType() == MessageType.ASSISTANT).count();
        long toolMsgCount = history.stream().filter(m -> m.getMessageType() == MessageType.TOOL).count();
        
        System.out.println("\n消息类型统计：");
        System.out.println("USER: " + userMsgCount);
        System.out.println("ASSISTANT: " + assistantMsgCount);
        System.out.println("TOOL: " + toolMsgCount);
        
        assertThat(userMsgCount).isEqualTo(1);
        assertThat(toolMsgCount).isGreaterThanOrEqualTo(1); // 应该有工具调用
    }

    @Test
    public void testStreamingToolCallWithMessageChain() {
        // 创建天气查询工具
        ToolCallback weatherTool = createWeatherTool();

        // 配置带工具的 ChatOptions
        DeepSeekChatOptions options = new DeepSeekChatOptions();
        options.setTools(List.of(weatherTool));

        String conversationId = "stream-tool-test";

        System.out.println("\n===== 测试流式工具调用 + 完整消息链保存 =====");
        System.out.print("AI 流式响应: ");
        
        chatClient.prompt("北京今天天气如何")
                .options(options)
                .advisors(a -> a.param(ChatMemoryConstants.CONVERSATION_ID, conversationId))
                .stream()
                .content(chunk -> System.out.print(chunk));

        System.out.println();

        // 验证历史记录（流式完成后应该自动保存）
        List<Message> history = chatMemory.get(conversationId, -1);
        System.out.println("\n流式调用后 ChatMemory 保存的历史消息：");
        for (int i = 0; i < history.size(); i++) {
            Message msg = history.get(i);
            System.out.println((i + 1) + ". " + msg.getMessageType() + ": " + 
                (msg.getContent().length() > 50 ? msg.getContent().substring(0, 50) + "..." : msg.getContent()));
        }

        long userMsgCount = history.stream().filter(m -> m.getMessageType() == MessageType.USER).count();
        long assistantMsgCount = history.stream().filter(m -> m.getMessageType() == MessageType.ASSISTANT).count();
        long toolMsgCount = history.stream().filter(m -> m.getMessageType() == MessageType.TOOL).count();

        System.out.println("\n消息类型统计：");
        System.out.println("USER: " + userMsgCount);
        System.out.println("ASSISTANT: " + assistantMsgCount);
        System.out.println("TOOL: " + toolMsgCount);
        
        // 注意：流式调用目前还不支持工具调用的完整消息链保存
        // 这是已知的限制，需要在 DefaultChatClient.stream() 中进一步增强
        // 当前只验证基本的用户消息和响应保存
        assertThat(history.size()).isGreaterThanOrEqualTo(2); // 至少有 user + assistant
        // TODO: 未来实现流式工具调用的完整消息链保存后，这里可以验证 tool message
    }

    /**
     * 创建天气查询工具
     */
    private ToolCallback createWeatherTool() {
        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return new ToolDefinition(
                    "get_weather",
                    "查询指定城市的天气信息",
                    """
                    {
                        "type": "object",
                        "properties": {
                            "city": {
                                "type": "string",
                                "description": "城市名称，如：北京、上海"
                            }
                        },
                        "required": ["city"]
                    }
                    """
                );
            }

            @Override
            public String call(String toolInput) {
                System.out.println("\n[工具执行] get_weather 被调用，参数: " + toolInput);
                // 模拟天气查询
                String result = """
                    {
                        "city": "北京",
                        "weather": "晴天",
                        "temperature": "20°C",
                        "humidity": "45%"
                    }
                    """;
                System.out.println("[工具执行] 返回结果: " + result);
                return result;
            }
        };
    }
}
