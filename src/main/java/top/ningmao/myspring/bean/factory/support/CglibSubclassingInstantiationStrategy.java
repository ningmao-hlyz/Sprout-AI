package top.ningmao.myspring.bean.factory.support;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;

/**
 * 使用 CGLIB 动态生成子类
 *
 * @author ningmao
 * @since 2025-4-30
 */
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {
    
    
    @Override
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        //TODO 感兴趣的小伙伴可以实现下
        throw new UnsupportedOperationException("CGLIB instantiation strategy is not supported");
    }
}
