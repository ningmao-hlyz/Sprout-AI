package top.ningmao.myspring.bean.factory.support;

import top.ningmao.myspring.bean.BeansException;
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
    
    /**
     * 根据名称查找BeanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException 如果找不到BeanDefintion
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;
    
    /**
     * 是否包含指定名称的BeanDefinition
     *
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);
    
    /**
     * 返回定义的所有bean的名称
     *
     * @return
     */
    String[] getBeanDefinitionNames();

}
