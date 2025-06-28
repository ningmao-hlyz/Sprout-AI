package top.ningmao.myspring.common.event;


import top.ningmao.myspring.context.ApplicationListener;

/**
 * @author ningmao
 * @since 2025-5-16
 */
public class CustomEventListener implements ApplicationListener<CustomEvent> {
    
    @Override
    public void onApplicationEvent(CustomEvent event) {
        System.out.println(this.getClass().getName());
    }
}
