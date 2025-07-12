package top.ningmao.myspring.aop.framework;
/**
 * 获取的工厂，定义了获取 AOP (面向切面编程) 代理对象的功能。
 *
 * @author NingMao
 * @since 2025-07-12
 */
public interface AopProxy {
    Object getProxy();
}
