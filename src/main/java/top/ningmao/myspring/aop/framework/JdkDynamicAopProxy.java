package top.ningmao.myspring.aop.framework;
import org.aopalliance.intercept.MethodInterceptor;

import top.ningmao.myspring.aop.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK 动态代理
 * 若方法匹配，则执行拦截器逻辑，否则直接调用目标方法。
 *
 * @author NingMao
 * @since 2025-06-28
 */public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }


    /**
     * 创建代理对象，代理的是接口
     */
    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                advised.getTargetSource().getTargetClass(),
                this
        );
    }


    /**
     * 调用代理方法时的处理逻辑：
     * - 若匹配方法：执行拦截器逻辑
     * - 否则：直接执行原方法
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
            // 匹配成功，执行拦截器逻辑
            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
            return methodInterceptor.invoke(
                    new ReflectiveMethodInvocation(
                            advised.getTargetSource().getTarget(),
                            method,
                            args
                    )
            );
        }
        // 匹配失败，直接执行原方法
        return method.invoke(advised.getTargetSource().getTarget(), args);
    }
}
