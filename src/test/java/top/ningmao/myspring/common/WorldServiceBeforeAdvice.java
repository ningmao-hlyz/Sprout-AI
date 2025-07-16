package top.ningmao.myspring.common;

import top.ningmao.myspring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author NingMao
 * @since 2025-07-12
 */
public class WorldServiceBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("BeforeAdvice: do something before the earth explodes");
    }
}
