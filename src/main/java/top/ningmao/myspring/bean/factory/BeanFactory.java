package top.ningmao.myspring.bean.factory;

import top.ningmao.myspring.bean.BeansException;

/**
 * bean 容器
 *
 * @author ningmao
 * @since 2025-4-29
 */
public interface BeanFactory {
    
    /**
     * 获取bean
     *
     * @param name
     * @return
     * @throws BeansException bean不存在时
     */
    Object getBean(String name) throws BeansException;
}
