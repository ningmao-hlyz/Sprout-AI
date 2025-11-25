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
     * 最简单的调用方式 - 发送文本，获取文本回复
     *
     * @param message 用户消息
     * @return AI 的回复文本
     */
    ChatResponse call(Prompt message);


    default String call(String message) {
        Prompt prompt = new Prompt(message);
        ChatResponse response = call(prompt);
        return response.getOutput();
    }
}
