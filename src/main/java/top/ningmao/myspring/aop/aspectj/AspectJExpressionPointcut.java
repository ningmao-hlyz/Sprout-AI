package top.ningmao.myspring.aop.aspectj;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import top.ningmao.myspring.aop.ClassFilter;
import top.ningmao.myspring.aop.MethodMatcher;
import top.ningmao.myspring.aop.Pointcut;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * AspectJ表达式切点
 *
 * @author NingMao
 * @since 2025-06-27
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    /**
     * 定义当前切点支持的AspectJ切点原语集合。
     * 这里的实现只支持EXECUTION原语，即匹配方法的执行。
     */
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        // 静态初始化块，添加支持的切点原语。
        // PointcutPrimitive.EXECUTION 用于匹配方法的执行连接点。
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    /**
     * 存储经过AspectJ PointcutParser解析后的切点表达式对象。
     * 所有的实际匹配逻辑都将委托给这个对象。
     */
    private final PointcutExpression pointcutExpression;

    /**
     * 构造函数，用于创建AspectJExpressionPointcut实例。
     *
     * @param expression AspectJ切点表达式字符串，例如 "execution(* com.example.service.*.*(..))"
     */
    public AspectJExpressionPointcut(String expression) {
        // 获取一个PointcutParser实例，该解析器支持指定的切点原语，
        // 并使用当前类的类加载器来解析和处理类型。
        PointcutParser pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                SUPPORTED_PRIMITIVES, this.getClass().getClassLoader());
        // 解析传入的切点表达式字符串，生成PointcutExpression对象。
        pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    /**
     * 实现ClassFilter接口的matches方法。
     * 用于判断给定的Class是否与当前切点表达式在类级别上匹配。
     *
     * @param clazz 要检查的Class对象。
     * @return 如果Class可能匹配切入点表达式定义的连接点，则返回true；否则返回false。
     */
    @Override
    public boolean matches(Class<?> clazz) {
        // 将类的匹配逻辑委托给底层的AspectJ PointcutExpression。
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    /**
     * 实现MethodMatcher接口的matches方法。
     * 用于判断给定Class中的Method是否与当前切点表达式在方法级别上匹配。
     * 注意：此方法仅在clazz已经通过ClassFilter的匹配后才会被调用。
     *
     * @param method     要检查的Method对象。
     * @param targetClass Method所属的类。
     * @return 如果Method与切入点表达式匹配，则返回true；否则返回false。
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // 将方法的匹配逻辑委托给底层的AspectJ PointcutExpression。
        // matchesMethodExecution返回一个MatchResult，alwaysMatches()检查是否总是匹配。
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    /**
     * 实现Pointcut接口的getClassFilter方法。
     * 返回一个ClassFilter实例，用于进行类匹配。
     *
     * @return 当前AspectJExpressionPointcut实例自身，因为它也实现了ClassFilter接口。
     */
    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    /**
     * 实现Pointcut接口的getMethodMatcher方法。
     * 返回一个MethodMatcher实例，用于进行方法匹配。
     *
     * @return 当前AspectJExpressionPointcut实例自身，因为它也实现了MethodMatcher接口。
     */
    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
}
