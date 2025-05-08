package top.ningmao.myspring.bean.factory.support;




import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.ConfigurableListableBeanFactory;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }
    
    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws org.springframework.beans.BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException("No bean named '" + beanName + "' is defined");
        }
        
        return beanDefinition;
    }
    
    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String,T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName,beanDefinition)->{
            Class beanClass = beanDefinition.getBeanClass();
            if(type.isAssignableFrom(beanClass)){
                T bean = (T)getBean(beanName);
                result.put(beanName,bean);
            }
        });
        return result;
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        Set<String> beanNames = beanDefinitionMap.keySet();
        return beanNames.toArray(new String[0]);
    }
}
