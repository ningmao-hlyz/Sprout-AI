package top.ningmao.myspring.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 环绕通知
 *
 * @author NingMao
 * @since 2025-07-12
 */
public interface AroundAdvice extends Advice {

    Object around(MethodInvocation invocation) throws Throwable;
}
