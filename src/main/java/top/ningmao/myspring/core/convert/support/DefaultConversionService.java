package top.ningmao.myspring.core.convert.support;


import top.ningmao.myspring.core.convert.converter.ConverterRegistry;

/**
 * 内部默认的转换服务
 *
 * @author NingMao
 * @since 2025-08-20
 */
public class DefaultConversionService extends GenericConversionService{

    public DefaultConversionService() {
        addDefaultConverters(this);
    }

    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
        //TODO 添加其他ConverterFactory
    }
}
