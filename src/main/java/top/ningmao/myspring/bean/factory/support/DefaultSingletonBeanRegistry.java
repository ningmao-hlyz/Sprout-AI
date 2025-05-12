package top.ningmao.myspring.bean.factory.support;

import cn.hutool.core.util.StrUtil;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.DisposableBean;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.SingletonBeanRegistry;

import java.util.ArrayList;
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
    
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();
    
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }
    
    public void destroySingletons() {
        ArrayList<String> beanNames = new ArrayList<>(disposableBeans.keySet());
        for (String beanName : beanNames) {
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
    }
    
    /**
     * 注册有销毁方法的bean，即bean继承自DisposableBean或有自定义的销毁方法
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }
}
