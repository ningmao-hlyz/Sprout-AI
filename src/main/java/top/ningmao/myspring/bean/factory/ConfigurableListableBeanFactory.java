package top.ningmao.myspring.bean.factory;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.config.AutowrieCapableBeanFactory;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.BeanPostProcessor;
import top.ningmao.myspring.bean.factory.config.ConfigurableBeanFactory;

/**
 * @author ningmao
 * @since 2025-5-8
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowrieCapableBeanFactory, ConfigurableBeanFactory {
    
    
    /**
     * 根据名称查找BeanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException 如果找不到BeanDefintion

     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;
    
    
    /**
     * 提前实例化所有单例实例
     *
     * @throws BeansException
     */
    void preInstantiateSingletons() throws org.springframework.beans.BeansException;
    
    @Override
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    
}
