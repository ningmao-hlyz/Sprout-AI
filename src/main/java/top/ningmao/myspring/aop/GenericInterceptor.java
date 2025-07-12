package top.ningmao.myspring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


/**
 * GenericInterceptor 类是一个通用的方法拦截器实现，它整合了多种类型的 AOP 通知（Advice）。
 * <p>
 * 该拦截器作为 AOP 框架的核心组件之一，负责在目标方法执行的不同阶段（前置、后置、返回、异常）
 * 动态地织入用户定义的增强逻辑。它通过组合不同类型的 Advice 接口实现这一功能。
 * <p>
 * 这个设计模式类似于 Spring AOP 中将多个 Advice 类型包装到一个拦截器链中，
 * 使得一个拦截器可以处理多种通知类型。
 *
 * @author NingMao
 * @since 2025-07-12
 */
public class GenericInterceptor implements MethodInterceptor {

    // 新增：环绕通知，功能最强大，可以完全控制方法执行
    private AroundAdvice aroundAdvice;

    // 前置通知：在目标方法执行之前执行的逻辑
    private BeforeAdvice beforeAdvice;

    // 返回通知：在目标方法成功执行并返回结果之后执行的逻辑
    private AfterReturningAdvice afterReturningAdvice;

    // 异常通知：在目标方法抛出异常之后执行的逻辑
    private ThrowsAdvice throwsAdvice;

    // 后置通知：在目标方法执行（无论成功或失败）之后执行的逻辑
    private AfterAdvice afterAdvice;

    /**
     * 配置环绕通知。
     * @param aroundAdvice 实现 AroundAdvice 接口的实例。
     */
    public void setAroundAdvice(AroundAdvice aroundAdvice) {
        this.aroundAdvice = aroundAdvice;
    }

    /**
     * 设置前置通知。
     * @param beforeAdvice 实现 BeforeAdvice 接口的实例。
     */
    public void setBeforeAdvice(BeforeAdvice beforeAdvice) {
        this.beforeAdvice = beforeAdvice;
    }

    /**
     * 设置返回通知。
     * @param afterReturningAdvice 实现 AfterReturningAdvice 接口的实例。
     */
    public void setAfterReturningAdvice(AfterReturningAdvice afterReturningAdvice) {
        this.afterReturningAdvice = afterReturningAdvice;
    }

    /**
     * 设置异常通知。
     * @param throwsAdvice 实现 ThrowsAdvice 接口的实例。
     */
    public void setThrowsAdvice(ThrowsAdvice throwsAdvice) {
        this.throwsAdvice = throwsAdvice;
    }

    /**
     * 设置后置通知。
     * @param afterAdvice 实现 AfterAdvice 接口的实例。
     */
    public void setAfterAdvice(AfterAdvice afterAdvice) {
        this.afterAdvice = afterAdvice;
    }


    /**
     * 拦截目标方法的调用，并根据配置的 Advice 类型织入增强逻辑。
     * 这是方法拦截器链的核心方法。
     *
     * @param invocation 包含目标方法、参数、目标对象等上下文信息的对象。
     * @return 目标方法执行后的结果。
     * @throws Throwable 如果目标方法或任何通知抛出异常。
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 如果配置了环绕通知，则优先执行环绕通知
        if (aroundAdvice != null) {
            // 环绕通知会通过 invocation.proceed() 来触发后续的通知链或目标方法执行
            return aroundAdvice.around(invocation);
        }


        Object result = null; // 用于存储目标方法执行的结果

        try {
            // 前置通知：如果配置了 BeforeAdvice，则在目标方法执行前调用其 before 方法
            if (beforeAdvice != null) {
                beforeAdvice.before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
            }

            // 执行目标方法的核心逻辑，并获取结果
            result = invocation.proceed();

        } catch (Exception throwable) { // 捕获目标方法执行过程中抛出的所有 Exception（及子类）
            // 异常通知：如果配置了 ThrowsAdvice，则在捕获到异常后调用其 throwsHandle 方法
            // 注意：这里捕获的是 Exception，如果 ThrowsAdvice 需要处理 Throwable，
            // 则需要将 catch (Exception throwable) 改为 catch (Throwable throwable)
            if (throwsAdvice != null) {
                // 将捕获到的异常、方法、参数和目标对象传递给异常通知处理器
                throwsAdvice.throwsHandle(throwable, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
            }
            // 重新抛出异常，以便调用链上层的处理器也能捕获
            // 注意：根据 AOP 规范，通常异常通知处理后会重新抛出，除非有特殊业务需求进行抑制
            throw throwable; // 重新抛出异常，确保异常行为传播
        } finally {
            // 后置通知：无论目标方法是否抛出异常，都会在方法执行结束后执行此处的逻辑
            if (afterAdvice != null) {
                afterAdvice.after(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
            }
        }

        // 返回通知：仅在目标方法成功执行（没有抛出异常）并返回结果后执行此处的逻辑
        // 注意：此部分位于 try-catch-finally 块之外，确保只有成功返回才触发
        if (afterReturningAdvice != null) {
            afterReturningAdvice.afterReturning(result, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        }

        // 返回目标方法的执行结果
        return result;
    }
}
