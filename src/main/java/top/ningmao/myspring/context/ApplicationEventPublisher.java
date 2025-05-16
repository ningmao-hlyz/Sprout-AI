package top.ningmao.myspring.context;


/**
 * 事件发布者接口
 *
 * @author ningmao
 * @since 2025-5-16
 */
public interface ApplicationEventPublisher {
    
    /**
     * 发布事件
     *
     * @param event
     */
    void publishEvent(ApplicationEvent event);
}
