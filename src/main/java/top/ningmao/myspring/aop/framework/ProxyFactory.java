package top.ningmao.myspring.aop.framework;

import top.ningmao.myspring.aop.AdvisedSupport;

/**
 * ProxyFactory 是 AOP 框架中的代理工厂类，用于根据配置生成目标对象的代理实例。
 *
 * @author NingMao
 * @since 2025-07-12
 */
public class ProxyFactory {

    /**
     * AOP 配置信息封装类，包含目标对象、通知链、是否使用类代理等信息
     */
    private AdvisedSupport advisedSupport;

    /**
     * 构造方法：初始化代理工厂，注入 AOP 配置信息
     *
     * @param advisedSupport 包含代理目标对象、拦截器等配置的支持类
     */
    public ProxyFactory(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
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
        // 如果设置为使用类代理（proxyTargetClass 为 true），则使用 CGLIB 动态字节码生成代理
        if (advisedSupport.isProxyTargetClass()) {
            return new CglibAopProxy(advisedSupport);
        }

        // 否则默认使用 JDK 的动态代理（要求目标对象必须实现接口）
        return new JdkDynamicAopProxy(advisedSupport);
    }
}
