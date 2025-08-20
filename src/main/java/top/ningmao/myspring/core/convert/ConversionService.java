package top.ningmao.myspring.core.convert;

/**
 * 与convert接口不同，convert接口是直接将source转换成targetType，而ConversionService接口是先判断是否可以转换然后再转换
 *
 * @author NingMao
 * @since 2025-08-19
 */
public interface ConversionService {

    /**
     * 判断是否可以转换
     */
    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    <T> T convert(Object source, Class<T> targetType);
}
