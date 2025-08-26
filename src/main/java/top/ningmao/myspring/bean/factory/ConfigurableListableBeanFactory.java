package top.ningmao.myspring.bean.factory;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.config.AutowrieCapableBeanFactory;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.BeanPostProcessor;
import top.ningmao.myspring.bean.factory.config.ConfigurableBeanFactory;
import top.ningmao.myspring.core.convert.ConversionService;
import top.ningmao.myspring.util.StringValueResolver;

/**
 * @author ningmao
 * @since 2025-5-8
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowrieCapableBeanFactory, ConfigurableBeanFactory {
    
    
    /**
     * 根据名称查找BeanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException 如果找不到BeanDefintion

     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    
    /**
     * 提前实例化所有单例实例
     *
     * @throws BeansException
     */
    void preInstantiateSingletons() throws BeansException;
    
    @Override
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    String resolveEmbeddedValue(String value);

    void setConversionService(ConversionService conversionService);

    ConversionService getConversionService();
}
