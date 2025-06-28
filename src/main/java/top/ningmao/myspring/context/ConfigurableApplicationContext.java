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
    
    /**
     * 关闭应用上下文
     */
    void close();
    
    /**
     * 向虚拟机中注册一个钩子方法，在虚拟机关闭之前执行关闭容器等操作
     */
    void registerShutdownHook();
}
