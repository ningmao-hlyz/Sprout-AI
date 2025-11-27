package top.ningmao.myspring.ai.chat.prompt.template;

import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.Collections;
import java.util.Map;


/**
 * Prompt Template（提示词模板）
 * 允许你定义带有变量占位符的模板字符串，然后用特定值渲染模板
 *
 * @author 宁猫
 * @since 2025-11-27 14:25:12
 */
public class PromptTemplate implements PromptTemplateActions, PromptTemplateMessageActions {

    private final String template;
    private final TemplateRenderer renderer;

    /**
     * 使用默认渲染器创建 Prompt Template
     *
     * @param template 模板字符串
     */
    public PromptTemplate(String template) {
        this(template, new DefaultTemplateRenderer());
    }

    /**
     * 使用自定义渲染器创建 Prompt Template
     *
     * @param template 模板字符串
     * @param renderer 模板渲染器
     */
    public PromptTemplate(String template, TemplateRenderer renderer) {
        if (template == null || template.isBlank()) {
            throw new IllegalArgumentException("Template cannot be null or empty");
        }
        this.template = template;
        this.renderer = renderer != null ? renderer : new DefaultTemplateRenderer();
    }

    // ==================== PromptTemplateStringActions 实现 ====================

    @Override
    public String render() {
        return render(Collections.emptyMap());
    }

    @Override
    public String render(Map<String, Object> model) {
        return renderer.apply(template, model);
    }

    // ==================== PromptTemplateMessageActions 实现 ====================

    @Override
    public Message createMessage() {
        return createMessage(Collections.emptyMap());
    }

    @Override
    public Message createMessage(Map<String, Object> model) {
        String content = render(model);
        return new UserMessage(content);
    }

    // ==================== PromptTemplateActions 实现 ====================

    @Override
    public Prompt create() {
        return create(Collections.emptyMap(), null);
    }

    @Override
    public Prompt create(ChatOptions modelOptions) {
        return create(Collections.emptyMap(), modelOptions);
    }

    @Override
    public Prompt create(Map<String, Object> model) {
        return create(model, null);
    }

    @Override
    public Prompt create(Map<String, Object> model, ChatOptions modelOptions) {
        Message message = createMessage(model);
        return new Prompt(Collections.singletonList(message), modelOptions);
    }


    /**
     * Builder 构建器
     */
    public static class Builder {
        private String template;
        private TemplateRenderer renderer = new DefaultTemplateRenderer();

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder renderer(TemplateRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public PromptTemplate build() {
            return new PromptTemplate(template, renderer);
        }
    }

    public static Builder builder() {
        return new Builder();
    }


    public String getTemplate() {
        return template;
    }
}
