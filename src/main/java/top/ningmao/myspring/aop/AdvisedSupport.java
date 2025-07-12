package top.ningmao.myspring.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * AOP 核心配置类
 * 1. TargetSource (目标对象源): 定义了被代理的原始对象。
 * 2. MethodInterceptor (方法拦截器): 包含了在目标方法执行前后或出现异常时需要执行的增强逻辑。
 * 3. MethodMatcher (方法匹配器): 用于判断哪些目标方法需要应用增强。
 *
 * @author NingMao
 * @since 2025-07-12
 */
public class AdvisedSupport {

    // 目标对象源：
    // 封装了被代理的原始对象（目标对象）。
    // 通过它可以获取到实际要执行业务逻辑的对象实例。
    private TargetSource targetSource;

    // 方法拦截器：
    // 这是 AOP 核心逻辑的实现。
    // 它定义了在目标方法执行前后（或出现异常时）要执行的增强逻辑（比如事务管理、日志记录、权限检查等）。
    private MethodInterceptor methodInterceptor;

    // 方法匹配器：
    // 用于判断一个方法是否应该被增强。
    // 例如，你可以通过它指定只有特定名称或特定签名的目标方法才应用通知（advice）。
    private MethodMatcher methodMatcher;

    /**
     * 获取目标对象源。
     * @return 目标对象源实例。
     */
    public TargetSource getTargetSource() {
        return targetSource;
    }

    /**
     * 设置目标对象源。
     * @param targetSource 目标对象源实例。
     */
    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    /**
     * 获取方法拦截器。
     * @return 方法拦截器实例。
     */
    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    /**
     * 设置方法拦截器。
     * @param methodInterceptor 方法拦截器实例。
     */
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    /**
     * 获取方法匹配器。
     * @return 方法匹配器实例。
     */
    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    /**
     * 设置方法匹配器。
     * @param methodMatcher 方法匹配器实例。
     */
    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }
}
