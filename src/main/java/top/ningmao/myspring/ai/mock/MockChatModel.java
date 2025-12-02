package top.ningmao.myspring.ai.mock;

import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.model.Generation;
import top.ningmao.myspring.ai.chat.model.StreamingChatModel;
import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.MockChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * MockChatModel - 模拟 AI 模型实现
 * 用于测试和演示多模型切换，不依赖真实 API
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class MockChatModel implements StreamingChatModel {

    /**
     * 默认配置选项
     */
    private final MockChatOptions defaultOptions;
    
    /**
     * 响应模板（Mock 特有的配置，不属于标准 ChatOptions）
     */
    private final String responseTemplate;

    /**
     * 完整构造函数
     *
     * @param defaultOptions   默认配置选项
     * @param responseTemplate 响应模板（包含 {input} 占位符）
     */
    public MockChatModel(MockChatOptions defaultOptions, String responseTemplate) {
        if (defaultOptions == null) {
            throw new IllegalArgumentException("Default options cannot be null");
        }
        this.defaultOptions = defaultOptions;
        this.responseTemplate = responseTemplate;
    }

    /**
     * 构造函数 - 使用默认响应模板
     *
     * @param defaultOptions 默认配置选项
     */
    public MockChatModel(MockChatOptions defaultOptions) {
        this(defaultOptions, null);
    }

    /**
     * 便捷构造函数 - 使用模型名称
     */
    public MockChatModel(String modelName) {
        this(new MockChatOptions(modelName), null);
    }

    /**
     * 便捷构造函数 - 使用模型名称和响应模板
     */
    public MockChatModel(String modelName, String responseTemplate) {
        this(new MockChatOptions(modelName), responseTemplate);
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        // 获取配置选项（优先使用 Prompt 中的配置）
        ChatOptions options = prompt.getOptions() != null ? prompt.getOptions() : defaultOptions;
        
        // 获取响应模板
        String template = getResponseTemplate(options);
        
        // 获取用户输入
        String userInput = extractUserInput(prompt);
        
        // 生成模拟响应
        String response = template.replace("{input}", userInput);
        
        // 构建 ChatResponse
        AssistantMessage assistantMessage = new AssistantMessage(response);
        Generation generation = new Generation(assistantMessage);
        List<Generation> generations = new ArrayList<>();
        generations.add(generation);
        
        return new ChatResponse(generations);
    }

    @Override
    public void stream(Prompt prompt, Consumer<String> chunkConsumer) {
        // 获取配置选项（优先使用 Prompt 中的配置）
        ChatOptions options = prompt.getOptions() != null ? prompt.getOptions() : defaultOptions;
        
        // 获取响应模板
        String template = getResponseTemplate(options);
        
        // 获取用户输入
        String userInput = extractUserInput(prompt);
        
        // 生成模拟响应
        String response = template.replace("{input}", userInput);
        
        // 模拟流式输出（逐字输出）
        for (char c : response.toCharArray()) {
            chunkConsumer.accept(String.valueOf(c));
            
            // 模拟网络延迟
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 从 Prompt 中提取用户输入
     */
    private String extractUserInput(Prompt prompt) {
        if (prompt.getMessages() == null || prompt.getMessages().isEmpty()) {
            return "";
        }
        
        // 获取最后一条用户消息
        return prompt.getMessages().stream()
                .filter(msg -> "user".equals(msg.getMessageType().getValue()))
                .reduce((first, second) -> second)
                .map(msg -> msg.getContent())
                .orElse("");
    }

    /**
     * 获取响应模板
     */
    private String getResponseTemplate(ChatOptions options) {
        // 如果构造时指定了自定义模板，使用自定义模板
        if (responseTemplate != null) {
            return responseTemplate;
        }
        
        // 使用默认模板
        String modelName = options.getModel() != null ? options.getModel() : "mock-model";
        return "[" + modelName + "] 收到您的消息: {input}，这是模拟响应。";
    }

    /**
     * 获取默认配置
     */
    public MockChatOptions getDefaultOptions() {
        return defaultOptions;
    }

    @Override
    public String getModelName() {
        // 从 defaultOptions 获取模型名称
        return defaultOptions != null && defaultOptions.getModel() != null 
            ? defaultOptions.getModel() 
            : "mock-model";
    }

    @Override
    public String toString() {
        return "MockChatModel{" +
                "model='" + getModelName() + '\'' +
                '}';
    }
}
