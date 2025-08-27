package top.ningmao.myspring.aop.framework;

import top.ningmao.myspring.aop.AdvisedSupport;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 通知器链工厂
 * 根据指定的目标方法和目标类，组装出一条“拦截器链
 *
 * @author NingMao
 * @since 2025-08-27
 */
public interface AdvisorChainFactory {

    /**
     * 根据目标方法和目标类，获取适用的拦截器链。
     *
     * @param config      AOP 配置信息（封装了目标对象、切点、增强等）
     * @param method      目标方法
     * @param targetClass 目标类
     * @return 拦截器链（按顺序执行）
     */
    List<Object> getInterceptorsAndDynamicInterceptionAdvice(
            AdvisedSupport config, Method method, Class<?> targetClass);

}
