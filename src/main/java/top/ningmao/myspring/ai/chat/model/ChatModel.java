package top.ningmao.myspring.ai.chat.model;

/**
 * 聊天模型接口 - 与 AI 模型交互的统一抽象
 * <p>
 * 这是最核心的接口，类似于 Spring 中的 BeanFactory
 * 所有 AI 模型提供商都需要实现这个接口
 * <p>
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
    String call(String message);
}
