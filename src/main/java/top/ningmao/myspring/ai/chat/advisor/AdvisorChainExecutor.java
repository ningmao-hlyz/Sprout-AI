package top.ningmao.myspring.ai.chat.advisor;

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
     * 执行响应后的 Advisor 链（逆序）
     */
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
