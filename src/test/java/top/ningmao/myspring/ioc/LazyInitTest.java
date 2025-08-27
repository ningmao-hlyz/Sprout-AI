package top.ningmao.myspring.ioc;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class LazyInitTest {

    @Test
    public void testLazyInit() throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:lazy-test.xml");
        System.out.println(System.currentTimeMillis() + ":applicationContext-over");
        TimeUnit.SECONDS.sleep(1);
        Car c = (Car) applicationContext.getBean("car");
        c.showTime();//显示bean的创建时间
    }
}
