package top.ningmao.myspring.bean.factory.support;

import top.ningmao.myspring.bean.factory.config.BeanDefinition;

/**
 *  Bean 的实例化策略接口
 *
 * @author ningmao
 * @since 2025-4-30
 */
public interface InstantiationStrategy {
    Object instantiate(BeanDefinition beanDefinition);
}
