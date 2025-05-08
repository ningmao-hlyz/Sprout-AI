package top.ningmao.myspring.bean.factory.support;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.core.io.DefaultResourceLoader;
import top.ningmao.myspring.core.io.ResourceLoader;

/**
 *
 *
 * @author ningmao
 * @since 2025-5-8
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private final BeanDefinitionRegistry registry;
    
    private ResourceLoader resourceLoader;
    
    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }
    
    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }
    
    @Override
    public void loadBeanDefinitions(String[] locations) throws BeansException {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }
    
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
    
}
