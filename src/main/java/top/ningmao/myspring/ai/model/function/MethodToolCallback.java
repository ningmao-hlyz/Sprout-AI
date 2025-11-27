package top.ningmao.myspring.ai.model.function;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


/**
 * 将带有 @Tool 注解的方法转换为可被 AI 调用的工具
 *
 * @author 宁猫
 * @since 2025-11-26 17:28:40
 */
public class MethodToolCallback implements ToolCallback {

    private final Object target;
    private final Method method;
    private final ToolDefinition toolDefinition;

    /**
     * 创建 Method Tool Callback
     *
     * @param target 目标对象
     * @param method 方法（必须带 @Tool 注解）
     */
    public MethodToolCallback(Object target, Method method) {
        if (!method.isAnnotationPresent(Tool.class)) {
            throw new IllegalArgumentException("Method must be annotated with @Tool");
        }

        this.target = target;
        this.method = method;
        this.method.setAccessible(true);
        this.toolDefinition = buildToolDefinition(method);
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    @Override
    public String call(String toolInput) {
        try {
            // 1. 解析输入参数
            Object[] args = parseArguments(toolInput);

            // 2. 调用方法
            Object result = method.invoke(target, args);

            // 3. 转换结果为字符串
            return result == null ? "" : result.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute tool: " + getName(), e);
        }
    }

    /**
     * 构建工具定义
     */
    private ToolDefinition buildToolDefinition(Method method) {
        Tool toolAnnotation = method.getAnnotation(Tool.class);

        // 工具名称
        String name = toolAnnotation.name().isEmpty() ? method.getName() : toolAnnotation.name();

        // 工具描述
        String description = toolAnnotation.description();

        // 生成 JSON Schema
        String inputSchema = generateJsonSchema(method);

        return new ToolDefinition(name, description, inputSchema);
    }

    /**
     * 生成输入参数的 JSON Schema
     */
    private String generateJsonSchema(Method method) {
        JSONObject schema = new JSONObject();
        schema.set("type", "object");

        JSONObject properties = new JSONObject();
        JSONArray required = new JSONArray();  // 改为数组！

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            String paramName = parameter.getName();
            ToolParam toolParam = parameter.getAnnotation(ToolParam.class);

            // 参数类型
            JSONObject paramSchema = new JSONObject();
            paramSchema.set("type", getJsonType(parameter.getType()));

            // 参数描述
            if (toolParam != null && !toolParam.description().isEmpty()) {
                paramSchema.set("description", toolParam.description());
            }

            properties.set(paramName, paramSchema);

            // 是否必需 - 添加到数组
            if (toolParam == null || toolParam.required()) {
                required.add(paramName);  // 添加参数名到数组
            }
        }

        schema.set("properties", properties);
        schema.set("required", required);

        return schema.toString();
    }

    /**
     * 将 Java 类型映射为 JSON Schema 类型
     */
    private String getJsonType(Class<?> type) {
        if (type == String.class) {
            return "string";
        } else if (type == int.class || type == Integer.class ||
                type == long.class || type == Long.class) {
            return "integer";
        } else if (type == double.class || type == Double.class ||
                type == float.class || type == Float.class) {
            return "number";
        } else if (type == boolean.class || type == Boolean.class) {
            return "boolean";
        } else if (type.isArray() || java.util.List.class.isAssignableFrom(type)) {
            return "array";
        } else {
            return "object";
        }
    }

    /**
     * 解析工具输入参数
     */
    private Object[] parseArguments(String toolInput) {
        Parameter[] parameters = method.getParameters();

        if (parameters.length == 0) {
            return new Object[0];
        }

        // 解析 JSON 输入
        JSONObject jsonInput = JSONUtil.parseObj(toolInput);

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String paramName = parameter.getName();
            Class<?> paramType = parameter.getType();

            // 从 JSON 中获取参数值
            Object value = jsonInput.get(paramName);

            // 类型转换
            args[i] = convertValue(value, paramType);
        }

        return args;
    }

    /**
     * 值类型转换
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return value;
        }

        // 字符串转换
        if (targetType == String.class) {
            return value.toString();
        }

        // 数值转换
        if (targetType == int.class || targetType == Integer.class) {
            return ((Number) value).intValue();
        }
        if (targetType == long.class || targetType == Long.class) {
            return ((Number) value).longValue();
        }
        if (targetType == double.class || targetType == Double.class) {
            return ((Number) value).doubleValue();
        }
        if (targetType == float.class || targetType == Float.class) {
            return ((Number) value).floatValue();
        }

        // 布尔转换
        if (targetType == boolean.class || targetType == Boolean.class) {
            return (Boolean) value;
        }

        return value;
    }

    @Override
    public String toString() {
        return "MethodToolCallback{" +
                "method=" + method.getName() +
                ", tool=" + toolDefinition.name() +
                '}';
    }
}
