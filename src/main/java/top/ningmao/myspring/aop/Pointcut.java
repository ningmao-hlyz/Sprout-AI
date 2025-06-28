package top.ningmao.myspring.aop;
/**
 * 切点抽象，定义切入点的蓝图
 *
 * @author NingMao
 * @since 2025-06-27
 */
public interface Pointcut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
