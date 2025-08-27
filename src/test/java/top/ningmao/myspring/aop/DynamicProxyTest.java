package top.ningmao.myspring.aop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import top.ningmao.myspring.aop.framework.CglibAopProxy;
import top.ningmao.myspring.aop.framework.JdkDynamicAopProxy;
import top.ningmao.myspring.aop.framework.ProxyFactory;
import top.ningmao.myspring.aop.framework.adapter.MethodAfterAdviceInterceptor;
import top.ningmao.myspring.aop.framework.adapter.MethodAfterReturningAdviceInterceptor;
import top.ningmao.myspring.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import top.ningmao.myspring.aop.framework.adapter.MethodThrowsAdviceInterceptor;
import top.ningmao.myspring.common.*;
import top.ningmao.myspring.service.WorldService;
import top.ningmao.myspring.service.WorldServiceImpl;

public class DynamicProxyTest {
    AdvisedSupport advisedSupport;

    /**
     * 测试 JDK 动态代理实现 AOP 的流程：
     * - 构建目标对象
     * - 设置 AOP 相关配置（拦截器、匹配器等）
     */
    @BeforeEach
    public void setup() throws Exception {
        // 1. 创建目标对象（被代理的对象）
        WorldService worldService = new WorldServiceImpl();

        // 2. 创建代理工厂（内部持有 AOP 配置信息 AdvisedSupport）
        advisedSupport = new ProxyFactory();

        // 3. 定义切点表达式：
        //    表示匹配 WorldService 接口中 explode(..) 方法的执行
        String expression = "execution(* top.ningmao.myspring.service.WorldService.explode(..))";

        // 4. 创建一个 Advisor（Advisor = Pointcut + Advice 的组合体）
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();

        // 4.1 设置切点表达式
        advisor.setExpression(expression);

        // 4.2 创建一个方法拦截器（AfterReturning 类型，方法执行成功后增强逻辑）
        //     将用户自定义的 MethodAfterReturningAdvice 封装为拦截器
        MethodAfterReturningAdviceInterceptor methodInterceptor =
                new MethodAfterReturningAdviceInterceptor(new WorldServiceAfterReturningAdvice());

        // 4.3 将拦截器设置到 Advisor 中
        advisor.setAdvice(methodInterceptor);

        // 5. 封装目标对象为 TargetSource（AOP 框架用来统一持有目标对象）
        TargetSource targetSource = new TargetSource(worldService);

        // 6. 将目标对象设置到代理工厂
        advisedSupport.setTargetSource(targetSource);

        // 7. 将定义好的 Advisor（切点 + 增强逻辑）加入到代理工厂
        advisedSupport.addAdvisor(advisor);
    }

    /**
     * 生成代理对象
     * 调用目标方法（如果匹配则执行增强逻辑）
     * @throws Exception
     */@Test
    public void testJdkDynamicProxy() throws Exception {
        WorldService proxy = (WorldService) new JdkDynamicAopProxy(advisedSupport).getProxy();
        proxy.explode();
    }

    /**
     * 生成代理对象
     * 调用目标方法（如果匹配则执行增强逻辑）
     * @throws Exception
     */@Test
    public void testCglibDynamicProxy() throws Exception {
        WorldService proxy = (WorldService) new CglibAopProxy(advisedSupport).getProxy();
        proxy.explode();
    }

    @Test
    public void testProxyFactory() throws Exception {
        // 使用JDK动态代理
        ProxyFactory factory = (ProxyFactory) advisedSupport;
        factory.setProxyTargetClass(false);
        WorldService proxy = (WorldService) factory.getProxy();
        proxy.explode();

        // 使用CGLIB动态代理
        factory.setProxyTargetClass(true);
        proxy = (WorldService) factory.getProxy();
        proxy.explode();
    }

    @Test
    public void testBeforeAdvice() throws Exception {
        // 1. 定义切点表达式，匹配 WorldService 接口的 explode 方法
        String expression = "execution(* top.ningmao.myspring.service.WorldService.explode(..))";

        // 2. 创建 AspectJ 切面，它是切点和通知的组合
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();

        // 2.1 为切面设置切点表达式 (在哪里拦截)
        advisor.setExpression(expression);

        // 3. 创建前置通知拦截器 (做什么增强)
        //    - WorldServiceBeforeAdvice 是用户自定义的“前置”增强逻辑
        MethodBeforeAdviceInterceptor methodInterceptor = new MethodBeforeAdviceInterceptor(new WorldServiceBeforeAdvice());

        // 3.1 为切面设置通知拦截器
        advisor.setAdvice(methodInterceptor);

        // 4. 将配置好的切面添加到 AOP 代理配置中
        advisedSupport.addAdvisor(advisor);

        // 5. 获取代理工厂实例
        ProxyFactory factory = (ProxyFactory) advisedSupport;

        // 6. 通过工厂创建代理对象
        WorldService proxy = (WorldService) factory.getProxy();

        // 7. 调用代理对象的方法，这将触发上面定义的前置通知
        proxy.explode();
    }

    @Test
    public void testAfterAdvice() throws Exception {
        // 1. 定义切点表达式，匹配 WorldService 接口的 explode 方法
        String expression = "execution(* top.ningmao.myspring.service.WorldService.explode(..))";

        // 2. 创建 AspectJ 切面，它是切点和通知的组合
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();

        // 2.1 为切面设置切点表达式 (在哪里拦截)
        advisor.setExpression(expression);

        // 3. 创建前置通知拦截器 (做什么增强)
        //    - WorldServiceAfterAdvice 是用户自定义的“后置”增强逻辑
        MethodAfterAdviceInterceptor methodInterceptor = new MethodAfterAdviceInterceptor(new WorldServiceAfterAdvice());

        // 3.1 为切面设置通知拦截器
        advisor.setAdvice(methodInterceptor);

        // 4. 将配置好的切面添加到 AOP 代理配置中
        advisedSupport.addAdvisor(advisor);

        // 5. 获取代理工厂实例
        ProxyFactory factory = (ProxyFactory) advisedSupport;

        // 6. 通过工厂创建代理对象
        WorldService proxy = (WorldService) factory.getProxy();

        // 7. 调用代理对象的方法，这将触发上面定义的前置通知
        proxy.explode();
    }

    @Test
    public void testAfterReturningAdvice() throws Exception {
        // 1. 定义切点表达式，匹配 WorldService 接口的 explode 方法
        String expression = "execution(* top.ningmao.myspring.service.WorldService.explode(..))";

        // 2. 创建 AspectJ 切面，它是切点和通知的组合
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();

        // 2.1 为切面设置切点表达式 (在哪里拦截)
        advisor.setExpression(expression);

        // 3. 创建前置通知拦截器 (做什么增强)
        //    - WorldAfterReturningAdvice 是用户自定义的“后置返回”增强逻辑
        MethodAfterReturningAdviceInterceptor methodInterceptor = new MethodAfterReturningAdviceInterceptor(new WorldServiceAfterReturningAdvice());

        // 3.1 为切面设置通知拦截器
        advisor.setAdvice(methodInterceptor);

        // 4. 将配置好的切面添加到 AOP 代理配置中
        advisedSupport.addAdvisor(advisor);

        // 5. 获取代理工厂实例
        ProxyFactory factory = (ProxyFactory) advisedSupport;

        // 6. 通过工厂创建代理对象
        WorldService proxy = (WorldService) factory.getProxy();

        // 7. 调用代理对象的方法，这将触发上面定义的前置通知
        proxy.explode();
    }

    @Test
    public void testThrowsAdvice() throws Exception {
        // 1. 定义切点表达式，匹配 WorldService 接口的 explode 方法
        String expression = "execution(* top.ningmao.myspring.service.WorldService.explode(..))";

        // 2. 创建 AspectJ 切面，它是切点和通知的组合
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();

        // 2.1 为切面设置切点表达式 (在哪里拦截)
        advisor.setExpression(expression);

        // 3. 创建前置通知拦截器 (做什么增强)
        //    - MethodThrowsAdviceInterceptor 是用户自定义的“异常抛出”增强逻辑
        MethodThrowsAdviceInterceptor methodInterceptor = new MethodThrowsAdviceInterceptor(new WorldServiceThrowsAdvice());

        // 3.1 为切面设置通知拦截器
        advisor.setAdvice(methodInterceptor);

        // 4. 将配置好的切面添加到 AOP 代理配置中
        advisedSupport.addAdvisor(advisor);

        // 5. 获取代理工厂实例
        ProxyFactory factory = (ProxyFactory) advisedSupport;

        // 6. 通过工厂创建代理对象
        WorldService proxy = (WorldService) factory.getProxy();

        // 7. 调用代理对象的方法，这将触发上面定义的前置通知
        proxy.explode();
    }


    @Test
    public void testAdvisor() throws Exception {
        // 1. 创建被代理的目标对象
        WorldService worldService = new WorldServiceImpl();

        // Advisor 是 Pointcut (切点) 和 Advice (通知) 的组合
        String expression = "execution(* top.ningmao.myspring.service.WorldService.explode(..))";

        // 2. 配置第一个切面：前置通知 (BeforeAdvice)
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(expression); // 定义切点
        MethodBeforeAdviceInterceptor methodInterceptor = new MethodBeforeAdviceInterceptor(new WorldServiceBeforeAdvice());
        advisor.setAdvice(methodInterceptor); // 定义通知

        // 3. 配置第二个切面：后置通知 (AfterReturningAdvice)
        AspectJExpressionPointcutAdvisor advisor1 = new AspectJExpressionPointcutAdvisor();
        advisor1.setExpression(expression); // 复用切点
        MethodAfterReturningAdviceInterceptor afterReturningAdviceInterceptor = new MethodAfterReturningAdviceInterceptor(new WorldServiceAfterReturningAdvice());
        advisor1.setAdvice(afterReturningAdviceInterceptor); // 定义通知

        // 4. 使用 ProxyFactory 创建代理
        ProxyFactory factory = new ProxyFactory();
        TargetSource targetSource = new TargetSource(worldService);
        factory.setTargetSource(targetSource);      // 设置目标对象
        factory.setProxyTargetClass(true);         // 强制使用 CGLIB 代理
        factory.addAdvisor(advisor);               // 添加第一个切面
        factory.addAdvisor(advisor1);              // 添加第二个切面

        // 5. 获取代理对象
        WorldService proxy = (WorldService) factory.getProxy();

        // 6. 调用方法，触发 AOP 通知链
        proxy.explode();
    }
//    @Test
//    public void testAllAdvice() throws Exception {
//        //设置before、after、afterReturning
//        GenericInterceptor methodInterceptor = new GenericInterceptor();
//        methodInterceptor.setBeforeAdvice(new WorldServiceBeforeAdvice());
//        methodInterceptor.setAfterAdvice(new WorldServiceAfterAdvice());
//        methodInterceptor.setAfterReturningAdvice(new WorldServiceAfterReturningAdvice());
//        advisedSupport.setMethodInterceptor(methodInterceptor);
//
//        WorldService proxy = (WorldService) new ProxyFactory(advisedSupport).getProxy();
//        proxy.explode();
//    }
//
//    @Test
//    public void testAllAdviceWithException() throws Exception {
//        WorldService worldService = new WorldServiceWithExceptionImpl();
//        //设置before、after、throws
//        GenericInterceptor methodInterceptor = new GenericInterceptor();
//        methodInterceptor.setBeforeAdvice(new WorldServiceBeforeAdvice());
//        methodInterceptor.setAfterAdvice(new WorldServiceAfterAdvice());
//        methodInterceptor.setThrowsAdvice(new WorldServiceThrowsAdvice());
//        advisedSupport.setMethodInterceptor(methodInterceptor);
//        advisedSupport.setTargetSource(new  TargetSource(worldService));
//
//        WorldService proxy = (WorldService) new ProxyFactory(advisedSupport).getProxy();
//        proxy.explode();
//    }
//
//    @Test
//    public void testAroundAdvice() throws Exception {
//         WorldServiceAroundAdvice aroundAdvice = new WorldServiceAroundAdvice();
//         GenericInterceptor methodInterceptor = new GenericInterceptor();
//         methodInterceptor.setAroundAdvice(aroundAdvice);
//         advisedSupport.setMethodInterceptor(methodInterceptor);
//         WorldService proxy = (WorldService) new ProxyFactory(advisedSupport).getProxy();
//         proxy.explode();
//    }
}
