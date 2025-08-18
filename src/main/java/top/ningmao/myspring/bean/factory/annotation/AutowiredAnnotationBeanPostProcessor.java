package top.ningmao.myspring.bean.factory.annotation;

import cn.hutool.core.bean.BeanUtil;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.PropertyValues;
import top.ningmao.myspring.bean.factory.BeanFactory;
import top.ningmao.myspring.bean.factory.BeanFactoryAware;
import top.ningmao.myspring.bean.factory.ConfigurableListableBeanFactory;
import top.ningmao.myspring.bean.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 处理@Autowired和@Value注解的BeanPostProcessor
 *
 * @author NingMao
 * @since 2025-07-17
 */
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        // 获取目标 bean 的 Class 对象
        Class<?> clazz = bean.getClass();

        // 获取类中所有的字段
        Field[] fields = clazz.getDeclaredFields();

        // 遍历字段，查找并处理 @Value 注解
        for (Field field : fields) {
            // 判断字段上是否标注了 @Value 注解
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (valueAnnotation != null) {
                // 获取注解中的占位符表达式，比如 "${jdbc.username}"
                String value = valueAnnotation.value();

                // 使用 BeanFactory 解析嵌套值（例如替换占位符）
                // 相当于调用 StringValueResolver.resolveStringValue
                value = beanFactory.resolveEmbeddedValue(value);

                // 使用工具类设置字段值（通过反射将解析后的值注入到字段中）
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }
        }

        //处理@Autowired注解
        for (Field field : fields) {
            Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
            if (autowiredAnnotation != null) {
                Class<?> fieldType = field.getType();
                String dependentBeanName = null;
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                Object dependentBean = null;
                if (qualifierAnnotation != null) {
                    dependentBeanName = qualifierAnnotation.value();
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                } else {
                    dependentBean = beanFactory.getBean(fieldType);
                }
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            }
        }

        // 返回原始的 PropertyValues，可能用于后续的其他处理
        return pvs;
    }


    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
