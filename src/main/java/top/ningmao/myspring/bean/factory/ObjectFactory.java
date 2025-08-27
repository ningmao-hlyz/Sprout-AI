package top.ningmao.myspring.bean.factory;


import top.ningmao.myspring.bean.BeansException;

/**
 * 获取对象工厂
 *
 * @author NingMao
 * @since 2025-08-26
 */
public interface ObjectFactory<T> {

    T getObject() throws BeansException;
}
