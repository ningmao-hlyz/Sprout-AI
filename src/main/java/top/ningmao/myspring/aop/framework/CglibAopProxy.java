package top.ningmao.myspring.aop.framework;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import top.ningmao.myspring.aop.AdvisedSupport;

import java.lang.reflect.Method;

/**
 * 使用 CGLIB 实现 AOP 动态代理
 * - 当目标类没有实现接口时，使用 CGLIB 是必要的。
 * - 核心是 Enhancer 创建目标类的子类，并用代理逻辑覆盖其方法。
 * <p></p>
 * 本类类似于 Spring 中的 CglibAopProxy 实现。
 * CGLIB 的拦截器是 net.sf.cglib.proxy.MethodInterceptor，与 AOP 联盟的 MethodInterceptor 不同。
 * 所以本类中做了一个适配处理。
 *
 * @author NingMao
 * @since 2025-07-10
 */public class CglibAopProxy implements AopProxy{

    private final AdvisedSupport advised; // AOP 配置支持类，封装了目标对象、拦截器、切点等信息

    public CglibAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    /**
     * 创建 CGLIB 代理对象
     */
    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();

        // 设置代理父类（即目标类）
        enhancer.setSuperclass(advised.getTargetSource().getTarget().getClass());

        // 设置代理实现的接口（可选，用于保持类型一致性）
        enhancer.setInterfaces(advised.getTargetSource().getTargetClass());

        // 设置方法拦截器（CGLIB 的 MethodInterceptor，不是 AOP 的）
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));

        // 创建代理实例
        return enhancer.create();
    }

    /**
     * 内部类：CGLIB 方法拦截器，适配 AOP 联盟的拦截器接口
     * <p></p>
     * 注意：
     * - 此处的 MethodInterceptor 是 CGLIB 包下的接口；
     * - 我们通过该类来桥接 Spring AOP 中的 MethodInterceptor（org.aopalliance 包）。
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        /**
         * 当代理对象方法被调用时，会进入该方法。
         * 判断是否需要增强（是否匹配切点）：
         * - 是：执行 AOP 拦截器逻辑；
         * - 否：直接调用原方法。
         */
        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

            // 构造一个方法调用对象（封装了 method、args、target）
            CglibMethodInvocation methodInvocation =
                    new CglibMethodInvocation(advised.getTargetSource().getTarget(), method, args, methodProxy);

            // 判断方法是否匹配切点表达式
            if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
                // 命中切点，执行 AOP 拦截器逻辑
                return advised.getMethodInterceptor().invoke(methodInvocation);
            }

            // 没命中切点，直接调用原方法
            return methodInvocation.proceed();
        }

    }


    /**
     * 方法调用封装类（专为 CGLIB 设计）
     * 继承自 ReflectiveMethodInvocation，重写了 proceed() 方法，使用 MethodProxy 执行调用
     */
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        /**
         * 在创建时就已经绑定了对应的方法，每个方法绑定一个 MethodProxy
         * 方法签名信息（类、方法名、参数类型、返回值）已经内置在 MethodProxy 对象中
         * 调用时不再需要重复传递方法名或签名信息。
         */
        private final MethodProxy methodProxy; // CGLIB 提供的高效方法代理对象

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
            super(target, method, arguments); // 调用父类构造保存 target/method/args
            this.methodProxy = methodProxy;
        }

        /**
         * 执行目标方法
         * - CGLIB 提供 MethodProxy 比反射更高效
         */
        @Override
        public Object proceed() throws Throwable {
            // 调用目标方法：目标对象 + 参数
            return this.methodProxy.invoke(this.target, this.arguments);
        }
    }
}
