package top.ningmao.myspring.bean.factory.config;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.ConfigurableListableBeanFactory;

/**
 * 允许自定义修改 BeanDefinition 的属性值
 *
 * @author ningmao
 * @since 2025-5-9
 */
public interface BeanFactoryPostProcessor {
    
    /**
     * 在所有BeanDefintion加载完成后，但在bean实例化之前，提供修改BeanDefinition属性值的机制
     *
     * @param beanFactory
     * @throws BeansException
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
    
}
