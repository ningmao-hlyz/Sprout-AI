package top.ningmao.myspring;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;
import top.ningmao.myspring.service.HelloService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ningmao
 * @since 2025-5-14
 */
public class AwareInterfaceTest {
    
    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        HelloService helloService = applicationContext.getBean("helloService", HelloService.class);
        assertThat(helloService.getApplicationContext()).isNotNull();
        assertThat(helloService.getBeanFactory()).isNotNull();
    }
}
