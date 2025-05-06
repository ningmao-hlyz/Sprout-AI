package top.ningmao.myspring;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Person;
import top.ningmao.myspring.bean.PropertyValue;
import top.ningmao.myspring.bean.PropertyValues;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
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
}
