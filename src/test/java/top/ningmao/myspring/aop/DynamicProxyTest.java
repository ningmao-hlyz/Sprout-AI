package top.ningmao.myspring.aop;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.aop.aspectj.AspectJExpressionPointcut;
import top.ningmao.myspring.aop.framework.JdkDynamicAopProxy;
import top.ningmao.myspring.common.WorldServiceInterceptor;
import top.ningmao.myspring.service.WorldService;
import top.ningmao.myspring.service.WorldServiceImpl;

public class DynamicProxyTest {

    /**
     * 测试 JDK 动态代理实现 AOP 的流程：
     * - 构建目标对象
     * - 设置 AOP 相关配置（拦截器、匹配器等）
     * - 生成代理对象
     * - 调用目标方法（如果匹配则执行增强逻辑）
     */
    @Test
    public void testJdkDynamicProxy() throws Exception {

        // 1. 创建目标对象（原始业务对象）
        WorldService worldService = new WorldServiceImpl();

        // 2. 构造 AOP 支持配置类 AdvisedSupport
        AdvisedSupport advisedSupport = new AdvisedSupport();

        // 3. 封装目标对象（包含目标类和其接口信息）
        TargetSource targetSource = new TargetSource(worldService);

        // 4. 定义方法拦截器（增强逻辑），例如日志、权限校验等
        WorldServiceInterceptor methodInterceptor = new WorldServiceInterceptor();

        // 5. 使用 AspectJ 表达式构建方法匹配器，只匹配 explode 方法
        // 表达式含义：匹配 WorldService 接口中名为 explode 的任意参数方法
        MethodMatcher methodMatcher =
                new AspectJExpressionPointcut("execution(* top.ningmao.myspring.service.WorldService.explode(..))")
                        .getMethodMatcher();

        // 6. 将 target、interceptor、matcher 设置到配置类中
        advisedSupport.setTargetSource(targetSource);
        advisedSupport.setMethodInterceptor(methodInterceptor);
        advisedSupport.setMethodMatcher(methodMatcher);

        // 7. 创建代理对象（JDK 动态代理，代理的是接口）
        WorldService proxy = (WorldService) new JdkDynamicAopProxy(advisedSupport).getProxy();

        // 8. 调用代理方法
        // 如果方法名为 explode，会匹配上表达式，执行拦截器逻辑
        proxy.explode();
    }
}
