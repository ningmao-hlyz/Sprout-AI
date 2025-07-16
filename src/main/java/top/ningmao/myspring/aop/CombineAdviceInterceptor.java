package top.ningmao.myspring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import top.ningmao.myspring.aop.framework.adapter.*;

/**
 * 组合 advice 拦截器
 *
 * @author NingMao
 * @since 2025-07-16
 */
public class CombineAdviceInterceptor implements MethodInterceptor {

    private MethodBeforeAdviceInterceptor beforeAdviceInterceptor;
    private MethodAfterAdviceInterceptor afterAdviceInterceptor;
    private MethodAfterReturningAdviceInterceptor afterReturningAdviceInterceptor;
    private MethodThrowsAdviceInterceptor throwsAdviceInterceptor;
    private MethodAroundAdviceInterceptor aroundAdviceInterceptor;

    public CombineAdviceInterceptor() {
    }
    public CombineAdviceInterceptor(MethodBeforeAdviceInterceptor beforeAdviceInterceptor,
                                    MethodAfterAdviceInterceptor afterAdviceInterceptor,
                                    MethodAfterReturningAdviceInterceptor afterReturningAdviceInterceptor,
                                    MethodThrowsAdviceInterceptor throwsAdviceInterceptor,
                                    MethodAroundAdviceInterceptor aroundAdviceInterceptor) {
        this.beforeAdviceInterceptor = beforeAdviceInterceptor;
        this.afterAdviceInterceptor = afterAdviceInterceptor;
        this.afterReturningAdviceInterceptor = afterReturningAdviceInterceptor;
        this.throwsAdviceInterceptor = throwsAdviceInterceptor;
        this.aroundAdviceInterceptor = aroundAdviceInterceptor;
    }
    public void setBeforeAdvice(MethodBeforeAdviceInterceptor beforeAdviceInterceptor) {
        this.beforeAdviceInterceptor = beforeAdviceInterceptor;
    }

    public void setAfterAdvice(MethodAfterAdviceInterceptor afterAdviceInterceptor) {
        this.afterAdviceInterceptor = afterAdviceInterceptor;
    }

    public void setAfterReturningAdvice(MethodAfterReturningAdviceInterceptor afterReturningAdviceInterceptor) {
        this.afterReturningAdviceInterceptor = afterReturningAdviceInterceptor;
    }

    public void setThrowsAdviceInterceptor(MethodThrowsAdviceInterceptor throwsAdviceInterceptor) {
        this.throwsAdviceInterceptor = throwsAdviceInterceptor;
    }

    public void setAroundAdvice(MethodAroundAdviceInterceptor aroundAdviceInterceptor) { // 更改参数类型
        this.aroundAdviceInterceptor = aroundAdviceInterceptor;
    }
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 如果配置了环绕通知，则优先执行环绕通知
        if (aroundAdviceInterceptor != null) {
            // 环绕通知会通过 invocation.proceed() 来触发后续的通知链或目标方法执行
            return aroundAdviceInterceptor.invoke(invocation);
        }


        Object result = null; // 用于存储目标方法执行的结果

        try {
            // 前置通知：如果配置了 BeforeAdvice，则在目标方法执行前调用其 before 方法
            if (beforeAdviceInterceptor != null) {
                beforeAdviceInterceptor.invoke(invocation);
            }

            // 执行目标方法的核心逻辑，并获取结果
            result = invocation.proceed();

        } catch (Exception throwable) { // 捕获目标方法执行过程中抛出的所有 Exception（及子类）
            // 异常通知：如果配置了 ThrowsAdvice，则在捕获到异常后调用其 throwsHandle 方法
            // 注意：这里捕获的是 Exception，如果 ThrowsAdvice 需要处理 Throwable，
            // 则需要将 catch (Exception throwable) 改为 catch (Throwable throwable)
            if (throwsAdviceInterceptor != null) {
                // 将捕获到的异常、方法、参数和目标对象传递给异常通知处理器
                throwsAdviceInterceptor.invoke(invocation);
            }
            // 重新抛出异常，以便调用链上层的处理器也能捕获
            // 注意：根据 AOP 规范，通常异常通知处理后会重新抛出，除非有特殊业务需求进行抑制
            throw throwable; // 重新抛出异常，确保异常行为传播
        } finally {
            // 后置通知：无论目标方法是否抛出异常，都会在方法执行结束后执行此处的逻辑
            if (afterAdviceInterceptor != null) {
                afterAdviceInterceptor.invoke(invocation);
            }
        }

        // 返回通知：仅在目标方法成功执行（没有抛出异常）并返回结果后执行此处的逻辑
        // 注意：此部分位于 try-catch-finally 块之外，确保只有成功返回才触发
        if (afterReturningAdviceInterceptor != null) {
            afterReturningAdviceInterceptor.invoke(invocation);
        }

        // 返回目标方法的执行结果
        return result;
    }


}
