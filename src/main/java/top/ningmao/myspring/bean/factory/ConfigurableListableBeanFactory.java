package top.ningmao.myspring.bean.factory;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;

/**
 *
 *
 * @author ningmao
 * @since 2025-5-8
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory{
    
    
    /**
     * 根据名称查找BeanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException 如果找不到BeanDefintion

     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;
}
