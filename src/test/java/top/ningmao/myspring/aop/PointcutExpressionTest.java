package top.ningmao.myspring.aop;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.aop.aspectj.AspectJExpressionPointcut;
import top.ningmao.myspring.service.HelloService;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class PointcutExpressionTest {


    @Test
    public void testPointcutExpression() throws Exception {

        /**
         * execution(
         *   返回类型: *,
         *   类路径: top.ningmao.myspring.service.HelloService,
         *   方法名: *,
         *   参数: (..)
         * )
         */
        // 创建切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* top.ningmao.myspring.service.HelloService.*(..))");
        // 获取类
        Class<HelloService> clazz = HelloService.class;
        // 获取方法
        Method method = clazz.getDeclaredMethod("sayHello");

        // 测试切点
        assertThat(pointcut.matches(clazz)).isTrue();
        // 测试方法
        assertThat(pointcut.matches(method, clazz)).isTrue();
        System.out.println(pointcut);
        System.out.println(pointcut.matches(clazz));
        System.out.println(pointcut.matches(method, clazz));
    }
}
