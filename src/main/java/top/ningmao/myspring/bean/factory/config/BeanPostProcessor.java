package top.ningmao.myspring.bean.factory.config;

import top.ningmao.myspring.bean.BeansException;

/**
 * 用于修改实例化后的 bean 的修改扩展点
 *
 * @author ningmao
 * @since 2025-5-9
 */
public interface BeanPostProcessor {
    
    /**
     * 在bean执行初始化方法之前执行此方法
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInitialization(Object bean,String beanName) throws BeansException;
    
    
    /**
     * 在bean执行初始化方法之后执行此方法
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
    
    
}
