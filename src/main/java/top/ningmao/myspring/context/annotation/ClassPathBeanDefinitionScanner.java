package top.ningmao.myspring.context.annotation;

import cn.hutool.core.util.StrUtil;
import top.ningmao.myspring.bean.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.support.BeanDefinitionRegistry;
import top.ningmao.myspring.stereotype.Component;

import java.util.Set;

/**
 * ClassPathBeanDefinitionScanner 用于扫描指定包路径下被 @Component 注解标注的类，
 * 并将其转换为 BeanDefinition 注册到 BeanDefinitionRegistry 中。
 * <p></p>
 * 该类继承自 ClassPathScanningCandidateComponentProvider，用于实际的类扫描逻辑。
 *
 * @author NingMao
 * @since 2025-07-17
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private BeanDefinitionRegistry registry; // 用于注册扫描得到的 BeanDefinition

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "top.ningmao.myspring.context.annotation.internalAutowiredAnnotationProcessor";
    /**
     * 扫描指定的包路径，并将所有标注了 @Component 的类注册为 BeanDefinition
     *
     * @param basePackages 要扫描的包名数组
     */
    public void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            // 获取当前包下所有符合条件（被 @Component 注解标注）的类对应的 BeanDefinition
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                // 解析 @Scope 注解，设置作用域（如果有）
                String beanScope = resolveBeanScope(candidate);
                if (StrUtil.isNotEmpty(beanScope)) {
                    candidate.setScope(beanScope);
                }
                // 生成 bean 的名称（使用 @Component 的 value 或类名首字母小写）
                String beanName = determineBeanName(candidate);
                // 将 BeanDefinition 注册到容器中
                registry.registerBeanDefinition(beanName, candidate);
            }
        }

        //注册处理@Autowired和@Value注解的BeanPostProcessor
        registry.registerBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    /**
     * 解析 @Scope 注解，提取作用域信息
     *
     * @param beanDefinition Bean 定义
     * @return 作用域值，如果没有指定则返回空字符串
     */
    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (scope != null) {
            return scope.value();
        }
        return StrUtil.EMPTY;
    }

    /**
     * 生成 Bean 的名称
     * <p></p>
     * 优先使用 @Component 中指定的 value 值，否则使用类名首字母小写的形式。
     *
     * @param beanDefinition Bean 定义
     * @return Bean 名称
     */
    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component component = beanClass.getAnnotation(Component.class);
        String value = component.value();
        if (StrUtil.isEmpty(value)) {
            // 默认使用类名首字母小写（例如 HelloService -> helloService）
            value = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }
}
