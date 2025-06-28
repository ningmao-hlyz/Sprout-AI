package top.ningmao.myspring.bean.factory.support;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.core.io.Resource;
import top.ningmao.myspring.core.io.ResourceLoader;

/**
 * 读取 bean 定义信息（BeanDefinition）的接口
 *
 * @author ningmao
 * @since 2025-5-8
 */
public interface BeanDefinitionReader {
    
    BeanDefinitionRegistry getRegistry();
    
    ResourceLoader getResourceLoader();
    
    void loadBeanDefinitions(Resource resource) throws BeansException;
    
    void loadBeanDefinitions(String location) throws BeansException;
    
    void loadBeanDefinitions(String[] locations) throws BeansException;
}
