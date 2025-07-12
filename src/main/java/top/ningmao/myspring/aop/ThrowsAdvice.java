package top.ningmao.myspring.aop;

import java.lang.reflect.Method;

/**
 * 抛出异常通知
 *
 * @author NingMao
 * @since 2025-07-12
 */
public interface ThrowsAdvice {
    void throwsHandle(Throwable throwable, Method method, Object[] args, Object target);

}
