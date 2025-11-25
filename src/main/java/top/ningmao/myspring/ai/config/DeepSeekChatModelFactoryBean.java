package top.ningmao.myspring.ai.config;

import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.deepseek.DeepSeekChatModel;
import top.ningmao.myspring.bean.factory.FactoryBean;


/**
 * DeepSeek ChatModel 工厂Bean
 * 使用 Sprout 框架的 FactoryBean 来创建 ChatModel 实例
 * <p>
 * 通过 XML 配置文件配置此 FactoryBean，容器会自动调用 getObject() 获取实际的 ChatModel
 *
 * @author 宁猫
 * @since 2025-11-25 20:53:56
 */
public class DeepSeekChatModelFactoryBean implements FactoryBean<ChatModel> {

    /**
     * API Key（通过 XML 配置或属性注入）
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model = "deepseek-chat";

    /**
     * 温度参数
     */
    private Float temperature = 0.7f;

    /**
     * 最大 Token 数，省钱哈
     */
    private Integer maxTokens = 2048;

    @Override
    public ChatModel getObject() throws Exception {
        // 如果没有配置 API Key，尝试从环境变量获取
        String actualApiKey = apiKey;
        if (actualApiKey == null || actualApiKey.isBlank()) {
            actualApiKey = System.getenv("DEEPSEEK_API_KEY");
        }

        if (actualApiKey == null || actualApiKey.isBlank()) {
            throw new IllegalStateException(
                    "DeepSeek API Key 未配置！\n" +
                    "请在 XML 配置文件中配置 apiKey 属性，\n" +
                    "或设置环境变量：export DEEPSEEK_API_KEY=your-key"
            );
        }

        // 创建配置选项
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();

        System.out.println("创建 DeepSeek ChatModel Bean: " + options);

        return new DeepSeekChatModel(actualApiKey, options);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Setter 方法供 XML 配置注入属性
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}
