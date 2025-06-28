package top.ningmao.myspring.common.event;


import top.ningmao.myspring.context.ApplicationListener;
import top.ningmao.myspring.context.event.ContextRefreshedEvent;

/**
 * @author ningmao
 * @since 2025-5-16
 */
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println(this.getClass().getName());
    }
}
