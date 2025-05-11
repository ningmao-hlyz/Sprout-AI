package top.ningmao.myspring.context.support;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;

/**
 * @author ningmao
 * @since 2025-5-10
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    
    private DefaultListableBeanFactory beanFactory;
    
    /**
     * 创建beanFactory并加载BeanDefinition
     *
     * @throws .BeansException
     */
    @Override
    protected final void refreshBeanFactory() throws BeansException {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }

    
    /**
     * 加载BeanDefinition
     *
     * @param beanFactory
     * @throws BeansException
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException;
    
    /**
     * 创建bean工厂
     *
     * @return
     */
    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }
    
    
    @Override
    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
