package top.ningmao.myspring.aop;


import java.lang.reflect.Method;

/**
 * @author NingMao
 * @since 2025-07-16
 */
public interface MethodBeforeAdvice extends BeforeAdvice {

    void before(Method method, Object[] args, Object target) throws Throwable;
}
