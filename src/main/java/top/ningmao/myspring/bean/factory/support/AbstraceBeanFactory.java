package top.ningmao.myspring.bean.factory.support;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.BeanPostProcessor;
import top.ningmao.myspring.bean.factory.config.ConfigurableBeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *  抽象的bean工厂
 *
 * @author ningmao
 * @since 2025-4-29
 */
public abstract class AbstraceBeanFactory extends DefaultSingletonBeanRegistry implements  ConfigurableBeanFactory{
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
   
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
    
    
    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor){
        //有则覆盖
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }
    
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
}
