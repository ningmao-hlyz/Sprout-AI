package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.prompt.Prompt;


/**
 * Advisor 接口
 * 用于在 ChatClient 调用前后对 Prompt 进行增强处理
 * </p>
 * 典型用途：
 * - 添加对话历史（MessageChatMemoryAdvisor）
 * - 添加 RAG 检索内容（QuestionAnswerAdvisor）
 * - 添加日志记录
 * - 修改参数
 *
 * @author 宁猫
 * @since 2025-11-28 14:43:19
 */
public interface Advisor {

    /**
     * 在调用模型前处理 Prompt
     *
     * @param prompt 原始 Prompt
     * @return 增强后的 Prompt
     */
    Prompt adviseRequest(Prompt prompt);

    /**
     * 在模型响应后处理（可选）
     *
     * @param response 模型响应内容
     * @return 处理后的响应内容
     */
    default String adviseResponse(String response) {
        return response;
    }
}
