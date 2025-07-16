package top.ningmao.myspring.bean.factory.config;


import org.springframework.beans.BeansException;

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
}
