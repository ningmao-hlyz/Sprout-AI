package top.ningmao.myspring.context.event;


import top.ningmao.myspring.context.ApplicationEvent;
import top.ningmao.myspring.context.ApplicationListener;

/**
 * @author ningmao
 * @since 2025-5-15
 */
public interface ApplicationEventMulticaster {
    
    void addApplicationListener(ApplicationListener<?> listener);
    
    void removeApplicationListener(ApplicationListener<?> listener);
    
    void multicastEvent(ApplicationEvent event);
}
