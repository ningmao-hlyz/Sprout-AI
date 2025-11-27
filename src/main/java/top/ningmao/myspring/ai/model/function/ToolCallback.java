package top.ningmao.myspring.ai.model.function;


/**
 * 定义一个可以被 AI 模型调用的工具，包括定义和执行逻辑
 *
 * @author 宁猫
 * @since 2025-11-26 17:25:54
 */
public interface ToolCallback {

    /**
     * 获取工具定义
     * AI 模型使用此定义来决定何时以及如何调用工具
     *
     * @return 工具定义
     */
    ToolDefinition getToolDefinition();

    /**
     * 执行工具并返回结果
     * AI 模型会将结果作为上下文来生成最终响应
     *
     * @param toolInput 工具输入（JSON 格式）
     * @return 工具执行结果（将发送回 AI 模型）
     */
    String call(String toolInput);

    /**
     * 获取工具名称
     *
     * @return 工具名称
     */
    default String getName() {
        return getToolDefinition().name();
    }
}
