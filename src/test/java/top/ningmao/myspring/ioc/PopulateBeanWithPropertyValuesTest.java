package top.ningmao.myspring.ioc;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.bean.Person;
import top.ningmao.myspring.bean.PropertyValue;
import top.ningmao.myspring.bean.PropertyValues;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.BeanReference;
import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 为 Bean 添加属性的测试类
 *
 * @author ningmao
 * @since 2025-4-30
 */
public class PopulateBeanWithPropertyValuesTest {
    
    @Test
    public void testPopulateBeanWithPropertyValues() {
        DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "ningmao"));
        propertyValues.addPropertyValue(new PropertyValue("age", 18));
        BeanDefinition beanDefinition = new BeanDefinition(Person.class, propertyValues);
        defaultListableBeanFactory.registerBeanDefinition("person", beanDefinition);
        
        Person person = (Person) defaultListableBeanFactory.getBean("person");
        System.out.println(person);
        assertThat(person.getName()).isEqualTo("ningmao");
        assertThat(person.getAge()).isEqualTo(18);
    }
    
    @Test
    /**
     * 依赖的情况，为bean注入bean
     */
    public void testPopulateBeanWithDependency() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        
        //注册Car实例
        PropertyValues propertyValuesForCar = new PropertyValues();
        propertyValuesForCar.addPropertyValue(new PropertyValue("brand", "porsche"));
        BeanDefinition carBeanDefinition = new BeanDefinition(Car.class, propertyValuesForCar);
        beanFactory.registerBeanDefinition("car", carBeanDefinition);
        
        //注册Person实例
        PropertyValues propertyValuesForPerson = new PropertyValues();
        propertyValuesForPerson.addPropertyValue(new PropertyValue("name", "derek"));
        propertyValuesForPerson.addPropertyValue(new PropertyValue("age", 18));
        //Person实例依赖Car实例
        propertyValuesForPerson.addPropertyValue(new PropertyValue("car", new BeanReference("car")));
        BeanDefinition beanDefinition = new BeanDefinition(Person.class, propertyValuesForPerson);
        beanFactory.registerBeanDefinition("person", beanDefinition);
        
        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
        assertThat(person.getName()).isEqualTo("derek");
        assertThat(person.getAge()).isEqualTo(18);
        Car car = person.getCar();
        assertThat(car).isNotNull();
        assertThat(car.getBrand()).isEqualTo("porsche");
    }
}
