package top.ningmao.myspring.bean.factory.config;

/**
 * 单例注册表
 *
 * @author ningmao
 * @since 2025-4-29
 */
public interface SingletonBeanRegistry {
    Object getSingleton(String beanName);
    
    void addSingleton(String beanName, Object singletonObject);
}
