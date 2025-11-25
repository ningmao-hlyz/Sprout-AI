package top.ningmao.myspring.ai.model;

import java.util.List;

/**
 * ModelResponse - 模型响应的基础接口
 *
 * @author 宁猫
 * @since 2025-11-25 18:47:45
 */
public interface ModelResponse<T> {

    /**
     * 获取模型生成的所有结果
     *
     * @return 结果列表
     */
    List<T> getResults();

    /**
     * 获取第一个结果
     *
     * @return 第一个结果
     */
    default T getResult() {
        List<T> results = getResults();
        return (results != null && !results.isEmpty()) ? results.get(0) : null;
    }
}
