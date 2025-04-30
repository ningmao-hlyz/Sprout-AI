package top.ningmao.myspring.bean.factory.support;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.BeanFactory;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;

/**
 *  抽象的bean工厂
 *
 * @author ningmao
 * @since 2025-4-29
 */
public abstract class AbstraceBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    @Override
    public Object getBean(String name)throws BeansException{
        Object bean = getSingleton(name);
        if(bean != null){
            return bean;
        }
        
        BeanDefinition beanDefinition = getBeanDefinition(name);
        return createBean(name,beanDefinition);
    }
    protected abstract BeanDefinition getBeanDefinition(String beanName)throws BeansException;
    protected abstract Object createBean(String bean,BeanDefinition beanDefinition);
}
