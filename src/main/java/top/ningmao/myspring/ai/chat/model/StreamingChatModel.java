package top.ningmao.myspring.ai.chat.model;

import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.function.Consumer;


/**
 * 支持流式响应的 ChatModel 接口
 *
 * @author 宁猫
 * @since 2025-11-26 15:32:00
 */
public interface StreamingChatModel extends ChatModel {

    /**
     * 流式调用 AI 模型
     * <p>
     * 与普通的 call() 方法不同，stream() 方法会在生成过程中持续回调 chunkConsumer，
     * 每次传递一个新生成的文本片段。
     *
     * @param prompt        提示词对象，包含消息和配置
     * @param chunkConsumer 文本片段消费者，每次 AI 生成新内容时被调用
     * @throws RuntimeException 如果调用失败
     */
    void stream(Prompt prompt, Consumer<String> chunkConsumer);

    /**
     * 流式调用 AI 模型（简化版本）
     * <p>
     * 直接传入字符串消息，内部会自动创建 Prompt 对象
     *
     * @param message       用户消息
     * @param chunkConsumer 文本片段消费者
     */
    default void stream(String message, Consumer<String> chunkConsumer) {
        stream(new Prompt(message), chunkConsumer);
    }
}
