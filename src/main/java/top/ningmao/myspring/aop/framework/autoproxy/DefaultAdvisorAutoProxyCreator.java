package top.ningmao.myspring.aop.framework.autoproxy;

import org.aopalliance.aop.Advice;
import top.ningmao.myspring.aop.*;
import top.ningmao.myspring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import top.ningmao.myspring.aop.framework.ProxyFactory;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.PropertyValues;
import top.ningmao.myspring.bean.factory.BeanFactory;
import top.ningmao.myspring.bean.factory.BeanFactoryAware;
import top.ningmao.myspring.bean.factory.config.InstantiationAwareBeanPostProcessor;
import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 默认的Advisor自动代理创建器
 *
 * @author NingMao
 * @since 2025-07-16
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    private Set<Object> earlyProxyReferences = new HashSet<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!earlyProxyReferences.contains(beanName)) {
            return wrapIfNecessary(bean, beanName);
        }

        return bean;
    }

    /**
     * 判断当前 bean 是否需要被 AOP 代理，如果需要则创建代理对象。
     *
     * 主要逻辑：
     * 1. 过滤基础设施类（如 Advisor、Advice 自身），避免出现死循环。
     * 2. 从容器中获取所有的 AspectJExpressionPointcutAdvisor（切面配置）。
     * 3. 遍历所有 Advisor，判断其切点是否匹配当前 bean 的 Class。
     * 4. 如果匹配：
     *    - 将 bean 封装成 TargetSource
     *    - 配置到 ProxyFactory 中
     *    - 添加对应的 Advisor（增强逻辑）
     *    - 设置 MethodMatcher（方法级别匹配）
     * 5. 如果最终 ProxyFactory 中存在 Advisor，则为该 bean 生成代理对象并返回。
     * 6. 否则，直接返回原始 bean。
     *
     * @param bean     当前正在创建的 bean
     * @param beanName bean 的名称
     * @return 如果需要代理则返回代理对象，否则返回原始 bean
     */
    protected Object wrapIfNecessary(Object bean, String beanName) {
        // 1. 基础设施类不做代理（如 Advisor / Advice 自身），避免死循环
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }

        // 2. 获取容器中所有切面配置（AspectJ 风格的 PointcutAdvisor）
        Collection<AspectJExpressionPointcutAdvisor> advisors =
                beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        try {
            ProxyFactory proxyFactory = new ProxyFactory();

            // 3. 遍历所有 Advisor，判断是否适用于当前 bean
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                ClassFilter classFilter = advisor.getPointcut().getClassFilter();

                if (classFilter.matches(bean.getClass())) {
                    // 3.1 封装目标对象
                    TargetSource targetSource = new TargetSource(bean);
                    proxyFactory.setTargetSource(targetSource);

                    // 3.2 添加切面增强（Advisor）
                    proxyFactory.addAdvisor(advisor);

                    // 3.3 设置方法匹配器（方法级别切点）
                    proxyFactory.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
                }
            }

            // 4. 如果存在适用的 Advisor，则为该 bean 创建代理
            if (!proxyFactory.getAdvisors().isEmpty()) {
                return proxyFactory.getProxy();
            }

        } catch (Exception ex) {
            // 5. 创建代理失败，抛出异常
            throw new BeansException("Error create proxy bean for: " + beanName, ex);
        }

        // 6. 如果没有匹配的 Advisor，直接返回原始 bean
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
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        earlyProxyReferences.add(beanName);
        return wrapIfNecessary(bean, beanName);
    }
}
