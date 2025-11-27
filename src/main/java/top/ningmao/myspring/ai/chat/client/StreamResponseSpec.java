package top.ningmao.myspring.ai.chat.client;

import top.ningmao.myspring.ai.chat.model.ChatResponse;

import java.util.function.Consumer;


/**
 * Stream Response 规范
 * 定义如何获取流式响应的 API
 *
 * @author 宁猫
 * @since 2025-11-27 15:40:13
 */
public interface StreamResponseSpec {

    /**
     * 流式获取内容（字符串）
     * 每次接收到一块内容时调用 consumer
     *
     * @param consumer 内容消费者
     */
    void content(Consumer<String> consumer);

    /**
     * 流式获取 ChatResponse
     * 每次接收到一块响应时调用 consumer
     *
     * @param consumer ChatResponse 消费者
     */
    void chatResponse(Consumer<ChatResponse> consumer);
}
