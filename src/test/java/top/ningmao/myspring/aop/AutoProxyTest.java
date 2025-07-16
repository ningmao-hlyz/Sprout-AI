package top.ningmao.myspring.aop;


import org.junit.jupiter.api.Test;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;
import top.ningmao.myspring.service.WorldService;

/**
 * @author NingMao
 * @since 2025-07-16
 */
public class AutoProxyTest {

    @Test
    public void testAutoProxy() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:auto-proxy.xml");
        WorldService worldService = applicationContext.getBean("worldService", WorldService.class);
        // 因为有环绕通知，所以是走到环绕通知，其余的可以自行测试（例如删掉配置aroundAdviceInterceptor）
        worldService.explode();
        WorldService worldServiceWithException = applicationContext.getBean("worldServiceWithException", WorldService.class);
        worldServiceWithException.explode();
    }

}
