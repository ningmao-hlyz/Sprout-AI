package top.ningmao.myspring.bean.factory;

import top.ningmao.myspring.bean.BeansException;

/**
 * 实现该接口，能感知所属BeanFactory
 *
 * @author ningmao
 * @since 2025-5-13
 */
public interface BeanFactoryAware extends Aware{
    
    void setBeanFactory(BeanFactory beanFactory)throws BeansException;
}
