package top.ningmao.myspring.ai.chat.prompt;


/**
 * DeepSeek 聊天配置选项
 *
 * @author 宁猫
 * @since 2025-11-25 20:51:55
 */
public class DeepSeekChatOptions implements ChatOptions {

    /**
     * 模型名称，默认使用 deepseek-chat
     */
    private String model = "deepseek-chat";

    /**
     * 温度参数 (0.0 - 2.0)
     * 较高的值（如 0.8）使输出更随机，较低的值（如 0.2）使输出更确定
     */
    private Float temperature = 0.7f;

    /**
     * 最大生成 token 数
     */
    private Integer maxTokens = 2048;

    /**
     * Top-P 采样参数 (0.0 - 1.0)
     * 核采样，模型考虑概率质量为 top_p 的 token
     */
    private Float topP = 1.0f;

    /**
     * 频率惩罚 (-2.0 - 2.0)
     * 正值会根据新 token 在文本中出现的频率对其进行惩罚
     */
    private Float frequencyPenalty = 0.0f;

    /**
     * 存在惩罚 (-2.0 - 2.0)
     * 正值会根据新 token 是否出现在文本中对其进行惩罚
     */
    private Float presencePenalty = 0.0f;

    public DeepSeekChatOptions() {
    }

    public DeepSeekChatOptions(String model) {
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
    public ChatOptions copy() {
        DeepSeekChatOptions copy = new DeepSeekChatOptions();
        copy.model = this.model;
        copy.temperature = this.temperature;
        copy.maxTokens = this.maxTokens;
        copy.topP = this.topP;
        copy.frequencyPenalty = this.frequencyPenalty;
        copy.presencePenalty = this.presencePenalty;
        return copy;
    }

    /**
     * Builder 模式，方便构建配置
     */
    public static class Builder {
        private final DeepSeekChatOptions options = new DeepSeekChatOptions();

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

        public DeepSeekChatOptions build() {
            return options;
        }
    }

    @Override
    public String toString() {
        return "DeepSeekChatOptions{" +
                "model='" + model + '\'' +
                ", temperature=" + temperature +
                ", maxTokens=" + maxTokens +
                ", topP=" + topP +
                ", frequencyPenalty=" + frequencyPenalty +
                ", presencePenalty=" + presencePenalty +
                '}';
    }
}
