package top.ningmao.myspring.context;


import top.ningmao.myspring.bean.BeansException;

/**
 * 可配置的应用上下文（扩展接口）
 *
 * @author ningmao
 * @since 2025-5-10
 */
public interface ConfigurableApplicationContext extends ApplicationContext{
    
    /**
     * 刷新容器
     *
     * @throws BeansException
     */
    void refresh() throws BeansException;
}
