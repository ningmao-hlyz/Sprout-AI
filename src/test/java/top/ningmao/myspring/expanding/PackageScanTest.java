package top.ningmao.myspring.expanding;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author NingMao
 * @since 2025-07-17
 */
public class PackageScanTest {

    @Test
    public void testScanPackage() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:package-scan.xml");

        Car car = applicationContext.getBean("car", Car.class);
        assertThat(car).isNotNull();
    }
}
