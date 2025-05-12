package top.ningmao.myspring.bean.factory.config;


import top.ningmao.myspring.bean.factory.HierarchicalBeanFactory;

/**
 * @author ningmao
 * @since 2025-5-8
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    
    /**
     *  Spring 容器中注册一个 BeanPostProcessor 实例
     *
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    
    /**
     * 销毁单例bean
     */
    void destroySingletons();

}
