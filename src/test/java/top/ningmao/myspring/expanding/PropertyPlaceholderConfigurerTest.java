package top.ningmao.myspring.expanding;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.bean.ServerConfig;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author NingMao
 * @since 2025-07-17
 */
public class PropertyPlaceholderConfigurerTest {

    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:property-placeholder-configurer.xml");
        ServerConfig serverConfig = applicationContext.getBean("serverConfig", ServerConfig.class);
        assertThat(serverConfig.getUrl()).isEqualTo("http://localhost:8080");
        assertThat(serverConfig.getPath()).isEqualTo("/api/dev");
        assertThat(serverConfig.getLocation()).isEqualTo("China");
    }
}
