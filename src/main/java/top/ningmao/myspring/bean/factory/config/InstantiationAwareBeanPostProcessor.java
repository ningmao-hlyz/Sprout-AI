package top.ningmao.myspring.bean.factory.config;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.PropertyValues;

/**
 * 在创建bean实例之前先执行
 *
 * @author NingMao
 * @since 2025-07-16
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 在bean实例化之前执行
     *
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

    /**
     * bean实例化之后，设置属性之前执行
     *
     * @param pvs
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
            throws BeansException;
}
