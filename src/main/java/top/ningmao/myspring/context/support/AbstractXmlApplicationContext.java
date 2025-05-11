package top.ningmao.myspring.context.support;


import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;
import top.ningmao.myspring.bean.factory.xml.XmlBeanDefinitionReader;

/**
 * @author ningmao
 * @since 2025-5-11
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext{
    
    protected abstract String[] getConfigLocations();
    
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }
    
    
}
