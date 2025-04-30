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
        // 1. 创建 CGLIB 的增强器对象（相当于代理工厂）
        Enhancer enhancer = new Enhancer();
        
        // 2. 设置要代理的目标类（即被代理类的“父类”）
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        
        // 3. 设置方法拦截器（这里用 lambda 表达式实现 MethodInterceptor）
        enhancer.setCallback((MethodInterceptor) (obj, method, argsTemp, proxy) ->
                proxy.invokeSuper(obj, argsTemp)
        );
        
        // 4. 创建代理对象（即目标类的子类实例）
        return enhancer.create();
    }
}
