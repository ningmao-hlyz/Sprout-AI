package top.ningmao.myspring.aop.aspectj;

import org.aopalliance.aop.Advice;
import top.ningmao.myspring.aop.Pointcut;
import top.ningmao.myspring.aop.PointcutAdvisor;
/**
 * 基于 AspectJ 表达式的 Advisor 实现
 * 该类将切点（Pointcut）与通知（Advice）进行绑定，构成完整的 Advisor。
 *
 * 作用：
 * 用于定义哪些类/方法匹配切面逻辑（Pointcut）
 * 绑定通知逻辑（Advice），如 MethodInterceptor、BeforeAdvice 等
 *
 * Spring 在解析 @Aspect 或 XML 配置切面时会构建该对象
 *
 * @author NingMao
 * @since 2025-07-16
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    // 用于匹配类与方法的切点表达式解析器（基于 AspectJ 表达式实现）
    private AspectJExpressionPointcut pointcut;

    // 通知逻辑，例如环绕通知（MethodInterceptor）等
    private Advice advice;

    // AspectJ 表达式字符串（例如 execution(* com.example..*Service.*(..))）
    private String expression;

    /**
     * 设置切点表达式（由容器注入或配置文件传入）
     * @param expression AspectJ 表达式字符串
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * 获取 Pointcut 对象
     * - 延迟初始化：第一次调用时构建 AspectJExpressionPointcut 实例
     */
    @Override
    public Pointcut getPointcut() {
        if (pointcut == null) {
            // 解析 expression 构造 Pointcut
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    /**
     * 获取 Advice（通知）对象
     */
    @Override
    public Advice getAdvice() {
        return advice;
    }

    /**
     * 设置 Advice（由容器注入或外部配置）
     * @param advice 通知逻辑（如环绕通知、前置通知等）
     */
    public void setAdvice(Advice advice) {
        this.advice = advice;
    }
}
