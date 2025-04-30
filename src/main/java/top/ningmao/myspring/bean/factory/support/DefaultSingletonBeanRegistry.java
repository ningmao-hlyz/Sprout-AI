package top.ningmao.myspring.bean.factory.support;

import top.ningmao.myspring.bean.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;
/**
 * 默认的添加单例 bean 注册工厂
 *
 * @author ningmao
 * @since 2025-4-29
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    
    private Map<String,Object> singletonObjects = new HashMap<>();
    
    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }
    
    protected void addSingleton(String beanName,Object singletonObject){
        singletonObjects.put(beanName,singletonObject);
    }
    
    
}
