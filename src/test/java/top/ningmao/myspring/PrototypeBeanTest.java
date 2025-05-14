package top.ningmao.myspring;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class PrototypeBeanTest {
    
    @Test
    public void testPrototype() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:prototype-bean.xml");
        
        Car car1 = applicationContext.getBean("car", Car.class);
        Car car2 = applicationContext.getBean("car", Car.class);
        assertThat(car1 != car2).isTrue();
    }
}
