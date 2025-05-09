package top.ningmao.myspring;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.Car;
import top.ningmao.myspring.bean.Person;
import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;
import top.ningmao.myspring.bean.factory.xml.XmlBeanDefinitionReader;
import top.ningmao.myspring.common.CustomBeanFactoryPostProcessor;
import top.ningmao.myspring.common.CustomerBeanPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ningmao
 * @since 2025-5-9
 */
public class BeanFactoryPostProcessorAndBeanPostProcessorTest {
    
    @Test
    public void testBeanFactoryPostProcessor() throws Exception{
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        
        //在所有BeanDefintion加载完成后，但在bean实例化之前，修改BeanDefinition的属性值
        CustomBeanFactoryPostProcessor beanFactoryPostProcessor = new CustomBeanFactoryPostProcessor();
        beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        
        Person person = (Person)beanFactory.getBean("person");
        System.out.println(person);
        //name属性在CustomBeanFactoryPostProcessor中被修改为ivy
        assertThat(person.getName()).isEqualTo("ivy");
    }
    
    @Test
    public void testBeanPostProcessor() throws Exception{
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        
        //添加bean实例化后的处理器
        CustomerBeanPostProcessor customerBeanPostProcessor = new CustomerBeanPostProcessor();
        beanFactory.addBeanPostProcessor(customerBeanPostProcessor);
        
        Car car = (Car) beanFactory.getBean("car");
        System.out.println(car);
        //brand属性在CustomerBeanPostProcessor中被修改为lamborghini
        assertThat(car.getBrand()).isEqualTo("lamborghini");
    }
}
