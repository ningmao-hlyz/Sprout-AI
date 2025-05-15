package top.ningmao.myspring.bean.factory;

/**
 * @author ningmao
 * @since 2025-5-15
 */
public interface FactoryBean<T> {
    
    T getObject() throws Exception;
    
    boolean isSingleton();
    
}
