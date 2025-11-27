package top.ningmao.myspring.ai.model.function;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来扫描类的 @tool 注解
 *
 * @author 宁猫
 * @since 2025-11-27 13:57:09
 */
public class ToolScanner {
    public static List<ToolCallback> scanClasses(Class<?>... classes) {
        List<ToolCallback> allTools = new ArrayList<>();
        for (Class<?> clazz : classes) {
            allTools.addAll(scanClass(clazz));
        }
        return allTools;
    }
    
    public static List<ToolCallback> scanClass(Class<?> clazz) {
        List<ToolCallback> tools = new ArrayList<>();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Tool.class)) {
                    tools.add(new MethodToolCallback(instance, method));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan tools from class: " + clazz.getName(), e);
        }
        return tools;
    }
}
