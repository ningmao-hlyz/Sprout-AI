package top.ningmao.myspring.bean.factory;

/**
 * 通俗地讲，FactoryBean 就像一个工厂。当 Spring 容器需要获取一个 FactoryBean 类型的 Bean 时
 * 它不会直接返回 FactoryBean 对象本身
 * 而是调用它的 getObject() 方法，将该方法的返回值作为真正的 Bean 注入到其他地方。
 *
 * @author ningmao
 * @since 2025-5-15
 */
public interface FactoryBean<T> {
    
    T getObject() throws Exception;
    
    boolean isSingleton();
    
}
