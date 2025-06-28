package top.ningmao.myspring.ioc;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

/**
 * @author ningmao
 * @since 2025-5-12
 */
public class InitAndDestoryMethonTest {
    
    @Test
    public void testInitAndDestroyMethod() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:init-and-destroy-method.xml");
        applicationContext.registerShutdownHook();  //或者手动关闭 applicationContext.close();
    }
}
