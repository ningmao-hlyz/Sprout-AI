package top.ningmao.myspring.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import top.ningmao.myspring.aop.AfterReturningAdvice;
import top.ningmao.myspring.aop.MethodAfterReturningAdvice;

/**
 * MethodAfterReturningAdvice
 *
 * @author NingMao
 * @since 2025-07-16
 */
public class MethodAfterReturningAdviceInterceptor implements MethodInterceptor {

    private MethodAfterReturningAdvice advice;

    public MethodAfterReturningAdviceInterceptor() {
    }

    public MethodAfterReturningAdviceInterceptor(MethodAfterReturningAdvice advice) {
        this.advice = advice;
    }

    public void setAdvice(MethodAfterReturningAdvice advice) {
        this.advice = advice;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        this.advice.afterReturning(retVal, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return retVal;
    }
}
