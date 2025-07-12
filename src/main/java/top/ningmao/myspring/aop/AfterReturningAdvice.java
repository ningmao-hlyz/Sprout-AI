package top.ningmao.myspring.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * 后置返回通知
 *
 * @author NingMao
 * @since 2025-07-12
 */
public interface AfterReturningAdvice extends Advice {
    void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}
