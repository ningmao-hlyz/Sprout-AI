package top.ningmao.myspring.ai.chat.model;


import top.ningmao.myspring.ai.chat.prompt.Prompt;

/**
 * 聊天模型接口 - 与 AI 模型交互的统一抽象
 * 所有 AI 模型提供商都需要实现这个接口
 *
 * @author ningmao
 * @since 2025-11-25
 */
public interface ChatModel {

    /**
     * 发送 Prompt，获取 ChatResponse
     *
     * @param prompt 包含消息和配置的 Prompt
     * @return AI 的完整响应
     */
    ChatResponse call(Prompt prompt);

    /**
     * 便捷方法 - 发送文本，获取文本回复
     *
     * @param message 用户消息
     * @return AI 的回复文本
     */
    default String call(String message) {
        Prompt prompt = new Prompt(message);
        ChatResponse response = call(prompt);
        return response.getOutput();
    }

    /**
     * 获取模型名称
     * 用于标识和注册模型，避免硬编码
     *
     * @return 模型名称（如 "gpt-4", "claude-3", "deepseek-chat"）
     */
    default String getModelName() {
        return "unknown-model";
    }
}
