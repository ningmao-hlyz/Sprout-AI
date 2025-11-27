package top.ningmao.myspring.ai.chat.prompt.template;

import java.util.Map;
import java.util.function.BiFunction;


/**
 * 模板渲染器接口
 * 负责将模板字符串中的占位符替换为实际值
 *
 * @author 宁猫
 * @since 2025-11-27 14:21:46
 */
public interface TemplateRenderer extends BiFunction<String, Map<String, Object>, String> {

    /**
     * 渲染模板
     *
     * @param template  模板字符串
     * @param variables 变量映射
     * @return 渲染后的字符串
     */
    @Override
    String apply(String template, Map<String, Object> variables);
}
