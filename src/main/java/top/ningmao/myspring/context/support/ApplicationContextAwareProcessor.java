package top.ningmao.myspring.context.support;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.config.BeanPostProcessor;
import top.ningmao.myspring.context.ApplicationContext;
import top.ningmao.myspring.context.ApplicationContextAware;

/**
 * @author ningmao
 * @since 2025-5-14
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    
    private final ApplicationContext applicationContext;
    
    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
