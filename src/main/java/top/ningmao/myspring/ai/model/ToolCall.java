package top.ningmao.myspring.ai.model;


/**
 * 工具调用
 * 表示 AI 模型请求调用的工具
 *
 * @author 宁猫
 * @since 2025-11-27 16:08:38
 */
public record ToolCall(
        String id,
        String type, // 通常是 function
        String functionName,
        String arguments
) {
}
