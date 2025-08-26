package top.ningmao.myspring.expanding;


import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author NingMao
 * @since 2025-08-26
 */
public class TypeConversionSecondPartTest {

    @Test
    public void testConversionService() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:type-conversion-second-part.xml");

        Car car = applicationContext.getBean("car", Car.class);
        assertThat(car.getPrice()).isEqualTo(1000000);
        assertThat(car.getProduceDate()).isEqualTo(LocalDate.of(2021, 1, 1));
    }
}
