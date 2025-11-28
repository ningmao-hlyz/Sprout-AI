package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.Map;

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
     * @param params 运行时参数（如 conversation_id）
     * @return 增强后的 Prompt
     */
    Prompt adviseRequest(Prompt prompt, Map<String, Object> params);

    /**
     * 在模型响应后处理（可选）
     *
     * @param prompt   原始 Prompt
     * @param response 模型响应内容
     * @param params   运行时参数
     * @return 处理后的响应内容
     */
    default String adviseResponse(Prompt prompt, String response, Map<String, Object> params) {
        return response;
    }

    /**
     * 获取 Advisor 名称
     */
    String getName();

    /**
     * 获取执行顺序（值越小优先级越高）
     * 默认为 0
     */
    default int getOrder() {
        return 0;
    }
}
