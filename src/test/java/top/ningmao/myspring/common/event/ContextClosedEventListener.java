package top.ningmao.myspring.common.event;

import top.ningmao.myspring.context.ApplicationListener;
import top.ningmao.myspring.context.event.ContextClosedEvent;

/**
 * @author ningmao
 * @since 2025-5-16
 */
public class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println(this.getClass().getName());
    }
}
