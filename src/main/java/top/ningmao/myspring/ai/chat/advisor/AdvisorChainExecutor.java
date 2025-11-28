package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Advisor 链执行器
 * 负责按顺序执行多个 Advisor
 *
 * @author 宁猫
 * @since 2025-11-28 15:47:20
 */
public class AdvisorChainExecutor {

    private final List<Advisor> advisors;

    public AdvisorChainExecutor(List<Advisor> advisors) {
        // 按 order 排序（值越小优先级越高）
        this.advisors = advisors.stream()
                .sorted(Comparator.comparingInt(Advisor::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 执行请求前的 Advisor 链
     */
    public Prompt adviseRequest(Prompt prompt, Map<String, Object> params) {
        Prompt currentPrompt = prompt;
        for (Advisor advisor : advisors) {
            currentPrompt = advisor.adviseRequest(currentPrompt, params);
        }
        return currentPrompt;
    }

    /**
     * 执行响应后的 Advisor 链（逆序，推荐使用）
     * 参考 Spring AI 的 CallAdvisorChain.nextCall 设计
     * </p>
     * <b>优势：</b>
     * - 传递完整的 ChatResponse 给 Advisor
     * - Advisor 可以访问工具调用的中间消息
     *
     * @param response ChatResponse 对象
     * @param params   运行时参数
     * @return 处理后的 ChatResponse
     */
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> params) {
        ChatResponse currentResponse = response;
        // 响应后按逆序执行
        for (int i = advisors.size() - 1; i >= 0; i--) {
            Advisor advisor = advisors.get(i);
            currentResponse = advisor.adviseResponse(currentResponse, params);
        }
        return currentResponse;
    }

    /**
     * 执行响应后的 Advisor 链（逆序，已废弃）
     * </p>
     * <b>请使用新方法：</b> {@link #adviseResponse(ChatResponse, Map)}
     *
     * @deprecated 使用 {@link #adviseResponse(ChatResponse, Map)} 代替
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public String adviseResponse(Prompt prompt, String response, Map<String, Object> params) {
        String currentResponse = response;
        // 响应后按逆序执行
        for (int i = advisors.size() - 1; i >= 0; i--) {
            Advisor advisor = advisors.get(i);
            currentResponse = advisor.adviseResponse(prompt, currentResponse, params);
        }
        return currentResponse;
    }

    /**
     * 检查是否有 Advisor
     */
    public boolean hasAdvisors() {
        return !advisors.isEmpty();
    }

    /**
     * 获取 Advisor 数量
     */
    public int size() {
        return advisors.size();
    }
}
