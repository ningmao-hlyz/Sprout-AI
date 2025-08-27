package top.ningmao.myspring.aop.framework;

import top.ningmao.myspring.aop.AdvisedSupport;

/**
 * ProxyFactory 是 AOP 框架中的代理工厂类
 * 用于根据配置生成目标对象的代理实例。
 *
 * @author NingMao
 * @since 2025-07-12
 */
public class ProxyFactory extends AdvisedSupport{



    public ProxyFactory() {
    }

    /**
     * 获取代理对象，对外提供的统一接口
     *
     * @return 代理对象（基于 JDK 动态代理或 CGLIB 字节码代理）
     */
    public Object getProxy() {
        // 创建 AopProxy，并调用其 getProxy 方法返回代理对象
        return createAopProxy().getProxy();
    }

    /**
     * 根据配置创建对应的 AopProxy 实例
     *
     * @return AopProxy 接口的实现类（JdkDynamicAopProxy 或 CglibAopProxy）
     */
    private AopProxy createAopProxy() {
        // 当需要使用类代理时：
        // 1. 如果 proxyTargetClass = true，则强制使用基于类的代理（CGLIB 方式）。
        // 2. 如果目标类没有实现任何接口（targetClass.length == 0），也只能使用 CGLIB。
        // 满足上述条件时，返回 CglibAopProxy 实例。
        if (this.isProxyTargetClass() || this.getTargetSource().getTargetClass().length == 0) {
            return new CglibAopProxy(this);
        }

        // 否则默认使用 JDK 的动态代理（要求目标对象必须实现接口）
        return new JdkDynamicAopProxy(this);
    }
}
