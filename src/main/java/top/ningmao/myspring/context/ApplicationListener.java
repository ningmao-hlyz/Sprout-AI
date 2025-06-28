package top.ningmao.myspring.context;

import java.util.EventListener;

/**
 * @author ningmao
 * @since 2025-5-15
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    
    void onApplicationEvent(E event);
}
