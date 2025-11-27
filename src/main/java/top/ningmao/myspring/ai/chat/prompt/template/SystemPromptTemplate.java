package top.ningmao.myspring.ai.chat.prompt.template;

import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;

import java.util.Collections;
import java.util.Map;


/**
 * System Prompt Template（系统提示词模板）
 * 专门用于创建系统角色的消息
 *
 * @author 宁猫
 * @since 2025-11-27 14:27:39
 */
public class SystemPromptTemplate implements PromptTemplateMessageActions {

    private final String template;
    private final TemplateRenderer renderer;

    /**
     * 使用默认渲染器创建 System Prompt Template
     *
     * @param template 模板字符串
     */
    public SystemPromptTemplate(String template) {
        this(template, new DefaultTemplateRenderer());
    }

    /**
     * 使用自定义渲染器创建 System Prompt Template
     *
     * @param template 模板字符串
     * @param renderer 模板渲染器
     */
    public SystemPromptTemplate(String template, TemplateRenderer renderer) {
        if (template == null || template.isBlank()) {
            throw new IllegalArgumentException("Template cannot be null or empty");
        }
        this.template = template;
        this.renderer = renderer != null ? renderer : new DefaultTemplateRenderer();
    }

    @Override
    public Message createMessage() {
        return createMessage(Collections.emptyMap());
    }

    @Override
    public Message createMessage(Map<String, Object> model) {
        String content = renderer.apply(template, model);
        return new SystemMessage(content);
    }

    /**
     * 渲染模板
     *
     * @param model 变量映射
     * @return 渲染后的字符串
     */
    public String render(Map<String, Object> model) {
        return renderer.apply(template, model);
    }

    public String getTemplate() {
        return template;
    }
}
