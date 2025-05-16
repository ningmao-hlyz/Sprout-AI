package top.ningmao.myspring;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.common.event.CustomEvent;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

/**
 * @author ningmao
 * @since 2025-5-16
 */
public class EventAndEventListenerTest {
    
    @Test
    public void testEventListener() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:event-and-event-listener.xml");
        applicationContext.publishEvent(new CustomEvent(applicationContext));
        
        applicationContext.registerShutdownHook();//或者applicationContext.close()主动关闭容器;
    }
}
