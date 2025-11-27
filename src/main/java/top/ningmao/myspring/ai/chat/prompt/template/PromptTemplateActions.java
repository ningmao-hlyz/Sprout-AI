package top.ningmao.myspring.ai.chat.prompt.template;

import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.Map;


/**
 * Prompt Template 操作接口
 * 设计用于返回可以传递给 ChatModel 的 Prompt 对象
 *
 * @author 宁猫
 * @since 2025-11-27 14:28:33
 */
public interface PromptTemplateActions extends PromptTemplateStringActions {

    /**
     * 创建 Prompt（无变量，无选项）
     *
     * @return Prompt 对象
     */
    Prompt create();

    /**
     * 创建 Prompt（无变量，带选项）
     *
     * @param modelOptions Chat 选项
     * @return Prompt 对象
     */
    Prompt create(ChatOptions modelOptions);

    /**
     * 创建 Prompt（带变量，无选项）
     *
     * @param model 变量映射
     * @return Prompt 对象
     */
    Prompt create(Map<String, Object> model);

    /**
     * 创建 Prompt（带变量，带选项）
     *
     * @param model        变量映射
     * @param modelOptions Chat 选项
     * @return Prompt 对象
     */
    Prompt create(Map<String, Object> model, ChatOptions modelOptions);
}
