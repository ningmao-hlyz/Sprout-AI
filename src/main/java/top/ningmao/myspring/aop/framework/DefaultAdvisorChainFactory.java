package top.ningmao.myspring.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import top.ningmao.myspring.aop.AdvisedSupport;
import top.ningmao.myspring.aop.Advisor;
import top.ningmao.myspring.aop.MethodMatcher;
import top.ningmao.myspring.aop.PointcutAdvisor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring 默认的 AdvisorChainFactory 实现类，用于生成“拦截器链”。
 *
 * @author NingMao
 * @since 2025-08-27
 */
public class DefaultAdvisorChainFactory implements AdvisorChainFactory{

    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(
            AdvisedSupport config, Method method, Class<?> targetClass) {

        // 1. 从配置中获取所有 Advisor
        Advisor[] advisors = config.getAdvisors().toArray(new Advisor[0]);

        // 2. 初始化拦截器链容器（大小预分配为 advisors.length）
        List<Object> interceptorList = new ArrayList<>(advisors.length);

        // 3. 确定实际的目标类（若为空则用声明该方法的类）
        Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());

        // 4. 遍历所有 Advisor
        for (Advisor advisor : advisors) {
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;

                // 4.1 校验当前 Advisor 是否适用于目标类
                if (pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
                    MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();

                    // 4.2 校验当前 Advisor 是否适用于目标方法
                    boolean match = mm.matches(method, actualClass);

                    if (match) {
                        // 4.3 获取增强逻辑（Advice），强转为 MethodInterceptor
                        MethodInterceptor interceptor = (MethodInterceptor) advisor.getAdvice();

                        // 4.4 加入拦截器链
                        interceptorList.add(interceptor);
                    }
                }
            }
        }

        // 5. 返回构建完成的拦截器链
        return interceptorList;
    }

}
