package top.ningmao.myspring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.bean.Person;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * XML 来创建Bean的一个测试
 *
 * @author ningmao
 * @since 2025-5-8
 */
public class XmlFIleDefineBeanTest {
    @Test
    public void testXmlFile() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        
        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
        assertThat(person.getName()).isEqualTo("derek");
        assertThat(person.getCar().getBrand()).isEqualTo("porsche");
        
        Car car = (Car) beanFactory.getBean("car");
        System.out.println(car);
        assertThat(car.getBrand()).isEqualTo("porsche");
    }
}
