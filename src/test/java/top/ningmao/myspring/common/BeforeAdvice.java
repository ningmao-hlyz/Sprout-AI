package top.ningmao.myspring.common;

import top.ningmao.myspring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author NingMao
 * @since 2025-08-26
 */
public class BeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Executing before advice");
    }
}
