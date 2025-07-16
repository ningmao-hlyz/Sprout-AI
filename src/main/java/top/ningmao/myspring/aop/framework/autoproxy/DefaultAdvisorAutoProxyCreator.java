package top.ningmao.myspring.aop.framework.autoproxy;


import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import top.ningmao.myspring.aop.*;
import top.ningmao.myspring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import top.ningmao.myspring.aop.framework.ProxyFactory;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.BeanFactory;
import top.ningmao.myspring.bean.factory.BeanFactoryAware;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.config.InstantiationAwareBeanPostProcessor;
import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;

import java.util.Collection;

/**
 * 默认的Advisor自动代理创建器
 *
 * @author NingMao
 * @since 2025-07-16
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;
    /**
     * 在 Bean 实例化之前进行处理（核心 AOP 创建代理逻辑）
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // 如果是 AOP 基础设施类（Advice/Pointcut/Advisor等），则不创建代理，防止死循环
        if (isInfrastructureClass(beanClass)) {
            return null;
        }

        // 从容器中获取所有 AspectJ 表达式的 Advisor（通知器）
        Collection<AspectJExpressionPointcutAdvisor> advisors =
                beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        try {
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                // 获取 advisor 的类过滤器，判断当前 beanClass 是否匹配
                ClassFilter classFilter = advisor.getPointcut().getClassFilter();
                if (classFilter.matches(beanClass)) {
                    // 满足切面条件，准备为该 Bean 创建代理对象

                    // 构造代理配置支持对象 AdvisedSupport
                    AdvisedSupport advisedSupport = new AdvisedSupport();

                    // 获取 bean 的定义（用于实例化）
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                    // 使用工厂中的实例化策略创建目标对象（注意：这一步不会注册到 Spring 容器中）
                    Object bean = beanFactory.getInstantiationStrategy().instantiate(beanDefinition);

                    // 设置代理的目标对象
                    TargetSource targetSource = new TargetSource(bean);
                    advisedSupport.setTargetSource(targetSource);

                    // 设置方法拦截器（实际就是 Advisor 中的通知 Advice）
                    advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());

                    // 设置方法匹配器（用于判断哪些方法需要织入通知）
                    advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());

                    // 创建代理对象并返回，替代原始 bean 进行注册
                    return new ProxyFactory(advisedSupport).getProxy();
                }
            }
        } catch (Exception ex) {
            // 处理异常情况，抛出 Bean 创建失败异常
            throw new BeansException("Error create proxy bean for: " + beanName, ex);
        }
        // 若未匹配到任何 Advisor，则不创建代理，返回 null 表示按默认流程实例化
        return null;
    }

    /**
     * 判断当前类是否是 AOP 基础设施类
     * 基础设施类本身不能被代理，否则会导致递归代理和死循环
     */
    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass)
                || Pointcut.class.isAssignableFrom(beanClass)
                || Advisor.class.isAssignableFrom(beanClass);
    }

    /**
     * 注入 BeanFactory，用于获取容器中已注册的 Advisor 及其他依赖
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    /**
     * 实例化后、初始化前的处理，这里未做任何处理，直接返回原对象
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws org.springframework.beans.BeansException {
        return bean;
    }

    /**
     * 初始化完成后的处理，这里未做任何处理，直接返回原对象
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
