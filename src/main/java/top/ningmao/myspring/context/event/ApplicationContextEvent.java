package top.ningmao.myspring.context.event;

import top.ningmao.myspring.context.ApplicationContext;
import top.ningmao.myspring.context.ApplicationEvent;

public abstract class ApplicationContextEvent extends ApplicationEvent {
    
    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }
    
    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }
}
