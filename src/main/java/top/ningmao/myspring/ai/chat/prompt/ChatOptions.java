package top.ningmao.myspring.ai.chat.prompt;

import top.ningmao.myspring.ai.model.ModelOptions;


/**
 * ChatOptions - 聊天模型的配置选项接口
 *
 * @author 宁猫
 * @since 2025-11-25 18:46:12
 */
public interface ChatOptions extends ModelOptions {

    /**
     * 获取模型名称
     */
    String getModel();

    /**
     * 获取温度参数 (0.0 - 2.0)
     * 控制输出的随机性
     */
    Float getTemperature();

    /**
     * 获取最大生成 token 数
     */
    Integer getMaxTokens();

    /**
     * 获取 Top-P 采样参数
     */
    Float getTopP();

    /**
     * 获取频率惩罚 (-2.0 - 2.0)
     */
    Float getFrequencyPenalty();

    /**
     * 获取存在惩罚 (-2.0 - 2.0)
     */
    Float getPresencePenalty();

    /**
     * 复制当前配置
     */
    ChatOptions copy();
}
