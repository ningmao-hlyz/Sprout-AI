package top.ningmao.myspring.common.event;


import top.ningmao.myspring.context.ApplicationContext;
import top.ningmao.myspring.context.event.ApplicationContextEvent;

/**
 * @author ningmao
 * @since 2025-5-16
 */
public class CustomEvent extends ApplicationContextEvent {
    
    public CustomEvent(ApplicationContext source) {
        super(source);
    }
}
