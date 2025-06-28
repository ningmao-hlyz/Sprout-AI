package top.ningmao.myspring.ioc;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author ningmao
 * @since 2025-5-15
 */
public class FactoryBeanTest {
    @Test
    public void testFactoryBean() throws Exception{
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:factory-bean.xml");
        Car car = applicationContext.getBean("car",Car.class);
        assertThat(car.getBrand()).isEqualTo("porsche");
    }
}
