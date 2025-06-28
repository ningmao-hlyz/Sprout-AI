package top.ningmao.myspring.bean.factory;

/**
 * @author ningmao
 * @since 2025-5-12
 */
public interface InitializingBean {
    
    void afterPropertiesSet() throws Exception;
}
