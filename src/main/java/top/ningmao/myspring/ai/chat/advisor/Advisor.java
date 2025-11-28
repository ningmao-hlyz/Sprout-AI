package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.Map;

/**
 * Advisor 接口
 * </p>
 * 典型用途：
 * - 添加对话历史（MessageChatMemoryAdvisor）
 * - 添加 RAG 检索内容（QuestionAnswerAdvisor）
 * - 添加日志记录
 * - 修改参数
 * </p>
 * <b>版本更新（参考 Spring AI）：</b>
 * - 新增 {@link #adviseResponse(ChatResponse, Map)} 方法，支持访问完整的 ChatResponse
 * - 这样可以保存工具调用的完整消息链（AssistantMessage with tool_calls + ToolMessage）
 * - 旧的 {@link #adviseResponse(Prompt, String, Map)} 方法标记为 @Deprecated，保持向后兼容
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
     * 在模型响应后处理（推荐使用）
     * 参考 Spring AI 的 CallAdvisor.adviseCall 返回值设计
     * </p>
     * <b>优势：</b>
     * - 可以访问完整的 ChatResponse，包括所有 Generation
     * - 可以获取工具调用的中间消息（AssistantMessage with tool_calls, ToolMessage）
     * - 支持保存完整的对话链
     *
     * @param response ChatResponse 完整响应对象
     * @param params   运行时参数
     * @return 处理后的 ChatResponse
     */
    default ChatResponse adviseResponse(ChatResponse response, Map<String, Object> params) {
        return response;
    }

    /**
     * 在模型响应后处理（已废弃，保持向后兼容）
     * </p>
     * <b>请使用新方法：</b> {@link #adviseResponse(ChatResponse, Map)}
     *
     * @param prompt   原始 Prompt（在新方法中不再需要）
     * @param response 模型响应内容字符串
     * @param params   运行时参数
     * @return 处理后的响应内容
     * @deprecated 使用 {@link #adviseResponse(ChatResponse, Map)} 代替
     */
    @Deprecated
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
