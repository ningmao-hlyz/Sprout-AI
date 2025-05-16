package top.ningmao.myspring.context.event;


import top.ningmao.myspring.context.ApplicationContext;

public class ContextClosedEvent extends ApplicationContextEvent {
    
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }
}
