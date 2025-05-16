package top.ningmao.myspring.context.event;


import top.ningmao.myspring.context.ApplicationContext;

/**
 * @author ningmao
 * @since 2025-5-16
 */
public class ContextRefreshedEvent  extends ApplicationContextEvent {
    
    public ContextRefreshedEvent(ApplicationContext source) {
        super(source);
    }
}
