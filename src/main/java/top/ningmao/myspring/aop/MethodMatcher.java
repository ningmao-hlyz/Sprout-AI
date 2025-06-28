package top.ningmao.myspring.aop;

import java.lang.reflect.Method;

/**
 * 定义方法匹配规则
 *
 * @author NingMao
 * @since 2025-06-27
 */
public interface MethodMatcher {

    /**
     * 匹配方法
     *
     * @param method  方法
     * @param targetClass   目标类
     * @return 是否匹配
     */
    boolean matches(Method method, Class<?> targetClass);
}
