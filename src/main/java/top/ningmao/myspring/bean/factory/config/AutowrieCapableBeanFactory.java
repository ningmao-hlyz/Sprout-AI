package top.ningmao.myspring.bean.factory.config;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.BeanFactory;
/**
 * @author ningmao
 * @since 2025-5-8
 */
public interface AutowrieCapableBeanFactory extends BeanFactory {
    
    /**
     * 执行BeanPostProcessors的postProcessBeforeInitialization方法
     *
     * @param existingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;
    
    /**
     * 执行BeanPostProcessors的postProcessAfterInitialization方法
     *
     * @param existingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;
}
