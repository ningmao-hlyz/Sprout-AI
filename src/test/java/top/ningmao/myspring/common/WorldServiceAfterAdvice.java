package top.ningmao.myspring.common;

import top.ningmao.myspring.aop.AfterAdvice;

import java.lang.reflect.Method;

/**
 * @author NingMao
 * @since 2025-07-12
 */
public class WorldServiceAfterAdvice implements AfterAdvice {
    @Override
    public void after(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("AfterAdvice: do something after the earth explodes");
    }
}
