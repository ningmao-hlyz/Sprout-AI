package top.ningmao.myspring.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ReflectiveMethodInvocation 封装了通过反射调用方法所需的一切信息：
 * 目标对象、目标方法和方法参数。
 * 它的核心是 `proceed()` 方法，用于实际执行被拦截的目标方法。
 *
 * @author NingMao
 * @since 2025-07-12
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    protected final Object proxy; // 代理对象

    protected final Object target;      // 被调用的目标对象

    protected final Method method;      // 被调用的方法

    protected final Object[] arguments; // 方法参数

    protected final Class<?> targetClass; // 目标对象对应的 Class

    protected final List<Object> interceptorsAndDynamicMethodMatchers; // 拦截器列表

    private int currentInterceptorIndex = -1; // 当前拦截器索引

    public ReflectiveMethodInvocation(Object proxy,Object target, Method method, Object[] arguments,Class<?> targetClass,List<Object> chain) {
        this.proxy=proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass=targetClass;
        this.interceptorsAndDynamicMethodMatchers=chain;
    }

    // 获取目标方法
    @Override
    public Method getMethod() {
        return method;
    }

    // 获取目标方法参数
    @Override
    public Object[] getArguments() {
        return arguments;
    }

    // 获取目标对象
    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object proceed() throws Throwable {
        // 初始currentInterceptorIndex为-1，每调用一次proceed就把currentInterceptorIndex+1
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            // 当调用次数 = 拦截器个数时
            // 触发当前method方法
            return method.invoke(this.target, this.arguments);
        }

        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        // 普通拦截器，直接触发拦截器invoke方法
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
    }



    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }
}
