package top.ningmao.myspring.bean.factory;

import top.ningmao.myspring.bean.BeansException;

import java.util.Map;

/**
 * 按照类型批量查找 Bean
 *
 * @author ningmao
 * @since 2025-5-6
 */
public interface ListableBeanFactory extends BeanFactory{
    
    /**
     * 返回指定类型的所有实例
     *
     * @param type
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;
    
    /**
     * 返回定义的所有bean的名称
     *
     * @return
     */
    String[] getBeanDefinitionNames();
}
