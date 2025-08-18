package top.ningmao.myspring.expanding;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Person;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author NingMao
 * @since 2025-08-19
 */
public class AutowriedAnnotationTest {


    @Test
    public void testAutowiredAnnotation() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:autowired-annotation.xml");

        Person person = applicationContext.getBean(Person.class);
        assertThat(person.getCar()).isNotNull();
        assertThat(person.getCar().getBrand()).isEqualTo("xiaomi");
    }
}
