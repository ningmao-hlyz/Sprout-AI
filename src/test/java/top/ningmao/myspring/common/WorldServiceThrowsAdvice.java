package top.ningmao.myspring.common;

import top.ningmao.myspring.aop.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * @author NingMao
 * @since 2025-07-12
 */
public class WorldServiceThrowsAdvice implements ThrowsAdvice {

    @Override
    public void throwsHandle(Throwable throwable, Method method, Object[] args, Object target) {
        System.out.println("ThrowsAdvice: do something when the earth explodes function throw an exception");
    }
}
