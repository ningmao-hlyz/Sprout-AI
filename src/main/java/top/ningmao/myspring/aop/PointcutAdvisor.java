package top.ningmao.myspring.aop;


/**
 * @author NingMao
 * @since 2025-07-16
 */
public interface PointcutAdvisor extends Advisor {

    Pointcut getPointcut();
}
