package top.ningmao.myspring.context.support;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.ConfigurableListableBeanFactory;
import top.ningmao.myspring.bean.factory.config.BeanFactoryPostProcessor;
import top.ningmao.myspring.bean.factory.config.BeanPostProcessor;
import top.ningmao.myspring.context.ConfigurableApplicationContext;
import top.ningmao.myspring.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * 抽象应用上下文
 *
 * @author ningmao
 * @since 2025-5-10
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    
    
    public abstract ConfigurableListableBeanFactory getBeanFactory();
    
    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }
    
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }
    
    /**
     * 注册BeanPostProcessor
     *
     * @param beanFactory
     */
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }
    
    /**
     * 在bean实例化之前，执行BeanFactoryPostProcessor
     *
     * @param beanFactory
     */
    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }
    
    /**
     * 创建BeanFactory，并加载BeanDefinition
     *
     * @throws BeansException
     */
    protected abstract void refreshBeanFactory() throws BeansException;
    
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws org.springframework.beans.BeansException {
        return getBeanFactory().getBeansOfType(type);
    }
    
    @Override
    public void refresh() throws BeansException {
        //创建BeanFactory，并加载BeanDefinition
        refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        
        //在bean实例化之前，执行BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors(beanFactory);
        
        //BeanPostProcessor需要提前与其他bean实例化之前注册
        registerBeanPostProcessors(beanFactory);
        
        //提前实例化单例bean
        beanFactory.preInstantiateSingletons();
    }
    
    @Override
    public void close() {
        doClose();
    }
    
    @Override
    public void registerShutdownHook() {
        Thread shutdownHook = new Thread() {
            @Override
            public void run() {
                doClose();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
    
    protected void doClose() {
        destroyBeans();
    }
    
    protected void destroyBeans() {
        getBeanFactory().destroySingletons();
    }
}
