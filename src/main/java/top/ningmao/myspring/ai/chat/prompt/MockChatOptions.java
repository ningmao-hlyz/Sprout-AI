package top.ningmao.myspring.ai.chat.prompt;

import top.ningmao.myspring.ai.model.function.ToolCallback;

import java.util.List;

/**
 * Mock 聊天配置选项
 * 用于测试和演示，不想买 api 了
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public class MockChatOptions implements ChatOptions {

    /**
     * 模型名称
     */
    private String model = "mock-model";

    /**
     * 温度参数（模拟用，不实际使用）
     */
    private Float temperature = 0.7f;

    /**
     * 最大生成 token 数（模拟用，不实际使用）
     */
    private Integer maxTokens = 2048;

    /**
     * Top-P 采样参数（模拟用，不实际使用）
     */
    private Float topP = 1.0f;

    /**
     * 频率惩罚（模拟用，不实际使用）
     */
    private Float frequencyPenalty = 0.0f;

    /**
     * 存在惩罚（模拟用，不实际使用）
     */
    private Float presencePenalty = 0.0f;

    /**
     * 工具列表
     */
    private List<ToolCallback> tools;

    public MockChatOptions() {
    }

    public MockChatOptions(String model) {
        this.model = model;
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    @Override
    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    @Override
    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public Float getTopP() {
        return topP;
    }

    public void setTopP(Float topP) {
        this.topP = topP;
    }

    @Override
    public Float getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Float frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    @Override
    public Float getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Float presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    @Override
    public List<ToolCallback> getTools() {
        return tools;
    }

    public void setTools(List<ToolCallback> tools) {
        this.tools = tools;
    }

    @Override
    public ChatOptions copy() {
        MockChatOptions copy = new MockChatOptions();
        copy.model = this.model;
        copy.temperature = this.temperature;
        copy.maxTokens = this.maxTokens;
        copy.topP = this.topP;
        copy.frequencyPenalty = this.frequencyPenalty;
        copy.presencePenalty = this.presencePenalty;
        copy.tools = this.tools;
        return copy;
    }

    /**
     * Builder 模式
     */
    public static class Builder {
        private final MockChatOptions options = new MockChatOptions();

        public Builder model(String model) {
            options.model = model;
            return this;
        }

        public Builder temperature(Float temperature) {
            options.temperature = temperature;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            options.maxTokens = maxTokens;
            return this;
        }

        public Builder topP(Float topP) {
            options.topP = topP;
            return this;
        }

        public Builder frequencyPenalty(Float frequencyPenalty) {
            options.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder presencePenalty(Float presencePenalty) {
            options.presencePenalty = presencePenalty;
            return this;
        }

        public Builder tools(List<ToolCallback> tools) {
            options.tools = tools;
            return this;
        }

        public MockChatOptions build() {
            return options;
        }
    }

    @Override
    public String toString() {
        return "MockChatOptions{" +
                "model='" + model + '\'' +
                ", temperature=" + temperature +
                ", maxTokens=" + maxTokens +
                ", topP=" + topP +
                ", frequencyPenalty=" + frequencyPenalty +
                ", presencePenalty=" + presencePenalty +
                ", tools=" + (tools != null ? tools.size() + " tools" : "null") +
                '}';
    }
}
