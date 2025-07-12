package top.ningmao.myspring.aop.framework;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
/**
 * ReflectiveMethodInvocation 封装了通过反射调用方法所需的一切信息：
 * 目标对象、目标方法和方法参数。
 * 它的核心是 `proceed()` 方法，用于实际执行被拦截的目标方法。
 *
 * @author NingMao
 * @since 2025-07-12
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    private final Object target;      // 被调用的目标对象

    private final Method method;      // 被调用的方法

    private final Object[] arguments; // 方法参数

    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
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

    // 获取方法执行器
    @Override
    public Object proceed() throws Throwable {
        return method.invoke(target, arguments);
    }



    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }
}
