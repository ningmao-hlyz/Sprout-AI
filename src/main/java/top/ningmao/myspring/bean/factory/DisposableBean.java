package top.ningmao.myspring.bean.factory;

/**
 * @author ningmao
 * @since 2025-5-12
 */
public interface DisposableBean {
    
    void destroy() throws Exception;
}
