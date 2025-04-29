package top.ningmao.myspring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.ningmao.myspring.bean.factory.BeanFactory;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * 测试
 *
 * @author ningmao
 * @since 2025-4-29
 */
class MySpringApplicationTests {
    
    @Test
    public void testGetBean(){
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.registerBean("helloService",new HelloService());
        Object bean = beanFactory.getBean("helloService");
        if(bean instanceof HelloService helloService){
            assertThat(helloService.sayHello()).isEqualTo("hello");
        }
    }
    
    class HelloService{
        public String sayHello(){
            System.out.println("hello");
            return "hello";
        }
    }
    
}
