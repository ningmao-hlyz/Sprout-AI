package top.ningmao.myspring.service;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.BeanFactory;
import top.ningmao.myspring.bean.factory.BeanFactoryAware;
import top.ningmao.myspring.context.ApplicationContext;
import top.ningmao.myspring.context.ApplicationContextAware;

/**
 * @author ningmao
 * @since 2025-5-13
 */
public class HelloService implements ApplicationContextAware, BeanFactoryAware {
   
    private ApplicationContext applicationContext;
    
    private BeanFactory beanFactory;
    public String sayHello() {
        System.out.println("hello");
        return "hello";
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
