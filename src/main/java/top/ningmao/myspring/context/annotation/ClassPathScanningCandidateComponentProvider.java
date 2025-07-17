package top.ningmao.myspring.context.annotation;

import cn.hutool.core.util.ClassUtil;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 用于扫描指定包路径下被 @Component 注解标注的类，并将其封装为 BeanDefinition。
 *
 * @author NingMao
 * @since 2025-07-17
 */
public class ClassPathScanningCandidateComponentProvider {
    /**
     * 查找指定包路径下所有被 @Component 注解标注的类，并返回对应的 BeanDefinition 集合。
     *
     * @param basePackage 要扫描的基础包名（例如 "com.example.service"）
     * @return 被识别为候选组件的 BeanDefinition 集合
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        // 使用 LinkedHashSet 保证扫描顺序
        Set<BeanDefinition> candidates = new LinkedHashSet<>();

        // 使用 Hutool 工具扫描指定包下所有被 @Component 注解标注的类
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);

        // 遍历所有符合条件的类，封装为 BeanDefinition 并添加到结果集中
        for (Class<?> clazz : classes) {
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            candidates.add(beanDefinition);
        }

        return candidates;
    }
}
