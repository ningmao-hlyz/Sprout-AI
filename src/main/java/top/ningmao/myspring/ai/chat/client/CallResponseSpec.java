package top.ningmao.myspring.ai.chat.client;

import top.ningmao.myspring.ai.chat.model.ChatResponse;


/**
 * Call Response 规范
 * 定义如何获取 AI 模型响应的流式 API
 *
 * @author 宁猫
 * @since 2025-11-27 14:52:49
 */
public interface CallResponseSpec {

    /**
     * 获取响应内容（字符串）
     *
     * @return AI 生成的文本内容
     */
    String content();

    /**
     * 获取完整的 ChatResponse 对象
     *
     * @return ChatResponse
     */
    ChatResponse chatResponse();
}
