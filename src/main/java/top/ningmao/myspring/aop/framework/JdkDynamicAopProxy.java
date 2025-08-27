package top.ningmao.myspring.aop.framework;
import org.aopalliance.intercept.MethodInterceptor;

import org.aopalliance.intercept.MethodInvocation;
import top.ningmao.myspring.aop.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * JDK 动态代理
 * 若方法匹配，则执行拦截器逻辑，否则直接调用目标方法。
 *
 * @author NingMao
 * @since 2025-06-28
 */

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

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
        // 1. 获取目标对象实例（被代理的对象）
        Object target = advised.getTargetSource().getTarget();
        Class<?> targetClass = target.getClass();
        Object retVal = null;

        // 2. 根据方法和目标类，获取适用的拦截器链（MethodInterceptor 列表）
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        if (chain == null || chain.isEmpty()) {
            // 3. 如果没有匹配的拦截器，则直接调用目标方法
            return method.invoke(target, args);
        } else {
            // 4. 如果有拦截器，将其与目标方法调用一起封装成 ReflectiveMethodInvocation
            MethodInvocation invocation =
                    new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);

            // 5. 按顺序执行拦截器链，最终调用目标方法
            retVal = invocation.proceed();
        }

        // 6. 返回方法执行结果（可能已被拦截器增强过）
        return retVal;
    }

}
