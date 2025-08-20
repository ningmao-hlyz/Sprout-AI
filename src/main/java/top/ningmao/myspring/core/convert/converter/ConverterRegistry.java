package top.ningmao.myspring.core.convert.converter;

/**
 * 类型转换器注册中心接口，用于管理和注册各种类型的转换器
 *
 * @author NingMao
 * @since 2025-08-19
 */
public interface ConverterRegistry {

    /**
     * 注册一个基础类型转换器
     */
    void addConverter(Converter<?, ?> converter);

    /**
     * 注册一个类型转换工厂
     */
    void addConverterFactory(ConverterFactory<?, ?> converterFactory);

    /**
     * 注册一个通用类型转换器
     */
    void addConverter(GenericConverter converter);
}
