package top.ningmao.myspring.aop.framework.autoproxy;


import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import top.ningmao.myspring.aop.*;
import top.ningmao.myspring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import top.ningmao.myspring.aop.framework.ProxyFactory;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.PropertyValues;
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
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 如果是基础设施类（比如 Advice、Advisor、Pointcut 本身），直接返回，避免给它们再创建代理，防止死循环
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }

        // 获取容器中所有的 Advisor（这里是表达式形式的 PointcutAdvisor）
        Collection<AspectJExpressionPointcutAdvisor> advisors =
                beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        try {
            // 遍历所有的 Advisor，找出匹配当前 bean 的那些
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                // 判断当前 advisor 的 ClassFilter 是否匹配 bean 的类型
                ClassFilter classFilter = advisor.getPointcut().getClassFilter();
                if (classFilter.matches(bean.getClass())) {
                    // 准备代理所需的配置信息
                    AdvisedSupport advisedSupport = new AdvisedSupport();

                    // 设置目标对象
                    TargetSource targetSource = new TargetSource(bean);
                    advisedSupport.setTargetSource(targetSource);

                    // 设置通知（MethodInterceptor）
                    advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());

                    // 设置方法匹配器（Pointcut 内部的 MethodMatcher）
                    advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());

                    // 创建代理并返回（此时拦截器和匹配器都起作用了）
                    return new ProxyFactory(advisedSupport).getProxy();
                }
            }
        } catch (Exception ex) {
            throw new BeansException("Error create proxy bean for: " + beanName, ex);
        }

        // 如果没有匹配的 Advisor，就直接返回原始 bean，不做代理
        return bean;
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
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws org.springframework.beans.BeansException {
        return null;
    }
}
