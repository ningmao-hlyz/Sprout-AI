package top.ningmao.myspring.ioc;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.A;
import top.ningmao.myspring.bean.B;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author NingMao
 * @since 2025-08-26
 */
public class CircularReferenceWithProxyBeanTest {

    @Test
    public void testCircularReference() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:circular-reference-with-proxy-bean.xml");
        A a = applicationContext.getBean("a", A.class);
        B b = applicationContext.getBean("b", B.class);

        //增加二级缓存不能解决有代理对象时的循环依赖。
        //a被代理，放进二级缓存earlySingletonObjects中的是实例化后的A，
        // 而放进一级缓存singletonObjects中的是被代理后的A，
        // 实例化b时从earlySingletonObjects获取a，所以b.getA() != a
        assertThat(b.getA() != a).isTrue();
    }
}
