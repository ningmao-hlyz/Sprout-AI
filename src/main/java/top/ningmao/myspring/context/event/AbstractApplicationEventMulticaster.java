package top.ningmao.myspring.context.event;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.BeanFactory;
import top.ningmao.myspring.bean.factory.BeanFactoryAware;
import top.ningmao.myspring.context.ApplicationEvent;
import top.ningmao.myspring.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ningmao
 * @since 2025-5-15
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {

    public final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new HashSet<>();
    
    private BeanFactory beanFactory;
    
    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }
    
    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
