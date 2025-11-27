package top.ningmao.myspring.ai.chat.prompt.template;

import java.util.Map;


/**
 * Prompt Template 字符串操作接口
 * 专注于创建和渲染提示词字符串
 *
 * @author 宁猫
 * @since 2025-11-27 14:24:01
 */
public interface PromptTemplateStringActions {

    /**
     * 渲染提示词模板（无变量）
     *
     * @return 渲染后的字符串
     */
    String render();

    /**
     * 渲染提示词模板（带变量）
     *
     * @param model 变量映射
     * @return 渲染后的字符串
     */
    String render(Map<String, Object> model);
}
