package top.ningmao.myspring.aop;

import java.lang.reflect.Method;
/**
 * 前置增强
 *
 * @author NingMao
 * @since 2025-07-12
 */
public interface BeforeAdvice {
    void before(Method method, Object[] args, Object target) throws Throwable;

}
