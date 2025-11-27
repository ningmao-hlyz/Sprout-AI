package top.ningmao.myspring.ai.chat.prompt.template;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 默认模板渲染器
 * 使用 {variable} 语法替换占位符
 *
 * @author 宁猫
 * @since 2025-11-27 14:22:28
 */
public class DefaultTemplateRenderer implements TemplateRenderer {

    private final char startDelimiter;

    private final char endDelimiter;

    private final Pattern pattern;

    /**
     * 默认构造函数，使用 {} 作为分隔符
     */
    public DefaultTemplateRenderer() {
        this('{', '}');
    }

    /**
     * 自定义分隔符构造函数
     *
     * @param startDelimiter 起始分隔符
     * @param endDelimiter   结束分隔符
     */
    public DefaultTemplateRenderer(char startDelimiter, char endDelimiter) {
        this.startDelimiter = startDelimiter;
        this.endDelimiter = endDelimiter;
        // 构建正则表达式，匹配 {variable} 或 <variable> 等格式
        String regex = Pattern.quote(String.valueOf(startDelimiter)) +
                "(\\w+)" +
                Pattern.quote(String.valueOf(endDelimiter));
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public String apply(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);

            if (value != null) {
                // 替换占位符为实际值
                matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
            } else {
                // 如果变量不存在，保留原样
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Builder 模式
     */
    public static class Builder {
        private char startDelimiter = '{';
        private char endDelimiter = '}';

        public Builder startDelimiterToken(char delimiter) {
            this.startDelimiter = delimiter;
            return this;
        }

        public Builder endDelimiterToken(char delimiter) {
            this.endDelimiter = delimiter;
            return this;
        }

        public DefaultTemplateRenderer build() {
            return new DefaultTemplateRenderer(startDelimiter, endDelimiter);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
