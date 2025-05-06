package top.ningmao.myspring.bean.factory.support;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 一个简单的 Bean 实例化策略实现
 *
 * @author ningmao
 * @since 2025-4-30
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {
    
    @Override
    public Object instantiate(BeanDefinition beanDefinition){
        Class beanClass = beanDefinition.getBeanClass();
        try {
            Constructor constructor = beanClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeansException("Failed to instantiate ["+ beanClass.getName() + "]", e);
        }
    }
}
