package top.ningmao.myspring.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * 后置通知
 *
 * @author NingMao
 * @since 2025-07-12
 */
public interface AfterAdvice extends Advice {

    void after(Method method, Object[] args, Object target) throws Throwable;
}
