package top.ningmao.myspring.bean.factory.support;

import top.ningmao.myspring.bean.factory.config.BeanDefinition;

/**
 * BeanDefinition 注册接口
 *
 * @author ningmao
 * @since 2025-4-29
 */
public interface BeanDefinitionRegistry {
    
    /**
     * 向注册表中注BeanDefinition
     *
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
