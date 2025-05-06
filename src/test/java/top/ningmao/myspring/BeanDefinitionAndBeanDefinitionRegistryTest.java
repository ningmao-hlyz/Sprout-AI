package top.ningmao.myspring;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;
import top.ningmao.myspring.service.HelloService;

/**
 * 测试 IOC1
 *
 * @author ningmao
 * @since 2025-4-29
 */
public class BeanDefinitionAndBeanDefinitionRegistryTest {
    @Test
    public void testBeanFactory() throws Exception {
       DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition beanDefinition = new BeanDefinition(HelloService.class);
        beanFactory.registerBeanDefinition("helloService", beanDefinition);
        
        HelloService helloService = (HelloService) beanFactory.getBean("helloService");
        helloService.sayHello();
    }
}
