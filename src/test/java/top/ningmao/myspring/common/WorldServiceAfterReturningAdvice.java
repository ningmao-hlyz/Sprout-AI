package top.ningmao.myspring.common;

import top.ningmao.myspring.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * @author NingMao
 * @since 2025-07-12
 */
public class WorldServiceAfterReturningAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("AfterReturningAdvice: do something after the earth explodes return");
    }

}
