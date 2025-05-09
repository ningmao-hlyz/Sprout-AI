package top.ningmao.myspring.common;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.PropertyValue;
import top.ningmao.myspring.bean.PropertyValues;
import top.ningmao.myspring.bean.factory.ConfigurableListableBeanFactory;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.BeanFactoryPostProcessor;

/**
 * @author ningmao
 * @since 2025-5-9
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition personBeanDefiniton = beanFactory.getBeanDefinition("person");
        PropertyValues propertyValues = personBeanDefiniton.getPropertyValues();
        //将person的name属性改为ivy
        propertyValues.addPropertyValue(new PropertyValue("name", "ivy"));
    }
}
