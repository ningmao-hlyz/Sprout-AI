package top.ningmao.myspring.context.support;


import top.ningmao.myspring.bean.factory.FactoryBean;
import top.ningmao.myspring.bean.factory.InitializingBean;
import top.ningmao.myspring.core.convert.ConversionService;
import top.ningmao.myspring.core.convert.converter.Converter;
import top.ningmao.myspring.core.convert.converter.ConverterFactory;
import top.ningmao.myspring.core.convert.converter.ConverterRegistry;
import top.ningmao.myspring.core.convert.converter.GenericConverter;
import top.ningmao.myspring.core.convert.support.DefaultConversionService;
import top.ningmao.myspring.core.convert.support.GenericConversionService;

import java.util.Set;
/**
 * 一个用于创建和配置 ConversionService 实例的 FactoryBean。
 * 这个类使得在 Spring IoC 容器中以声明式的方式配置一个共享的 ConversionService 变得非常方便。
 *
 * @author NingMao
 * @since 2025-08-25
 */
public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {

    /**
     * 用于存储用户通过配置注入的转换器集合。
     * 这些转换器可以是 Converter, ConverterFactory, 或 GenericConverter 类型。
     */
    private Set<?> converters;

    /**
     * FactoryBean 内部管理和配置的 ConversionService 实例。
     * 在 afterPropertiesSet 方法中被初始化。
     */
    private GenericConversionService conversionService;

    /**
     * InitializingBean 接口的方法，在 FactoryBean 的属性被容器设置之后调用。
     * 这里是进行初始化工作的核心：
     * 1. 创建一个 DefaultConversionService 实例，它预注册了很多默认的转换器。
     * 2. 调用 registerConverters 方法将用户自定义的转换器注册进去。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        conversionService = new DefaultConversionService();
        registerConverters(converters, conversionService);
    }

    /**
     * 一个辅助方法，用于遍历用户提供的 converters 集合，
     * 并将它们注册到指定的 ConverterRegistry (也就是我们的 conversionService) 中。
     *
     * @param converters 用户定义的转换器集合
     * @param registry   转换器注册表，通常就是 ConversionService 实例
     */
    private void registerConverters(Set<?> converters, ConverterRegistry registry) {
        if (converters != null) {
            for (Object converter : converters) {
                // 根据转换器的不同类型，调用 registry 对应的方法进行注册
                if (converter instanceof GenericConverter) {
                    registry.addConverter((GenericConverter) converter);
                } else if (converter instanceof Converter<?, ?>) {
                    registry.addConverter((Converter<?, ?>) converter);
                } else if (converter instanceof ConverterFactory<?, ?>) {
                    registry.addConverterFactory((ConverterFactory<?, ?>) converter);
                } else {
                    // 如果传入了不支持的类型，则抛出异常
                    throw new IllegalArgumentException("每个转换器对象必须实现 Converter, ConverterFactory, 或 GenericConverter 接口之一");
                }
            }
        }
    }

    /**
     * FactoryBean 接口的核心方法。
     * 当容器需要获取这个 FactoryBean "生产" 的 bean 时，会调用此方法。
     * 在这里，它返回已经配置好的 conversionService 实例。
     *
     * @return 配置完成的 ConversionService 实例
     */
    @Override
    public ConversionService getObject() throws Exception {
        return conversionService;
    }

    /**
     * FactoryBean 接口的方法，用于指示 getObject() 方法返回的对象是否是单例。
     * true 表示是单例，容器会缓存这个对象，后续请求都会返回同一个实例。
     *
     * @return true
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 标准的 setter 方法，用于让 Spring 容器通过依赖注入（DI）
     * 将在配置文件中定义的转换器集合注入到这个 FactoryBean 中。
     *
     * @param converters 转换器 bean 的集合
     */
    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

}
