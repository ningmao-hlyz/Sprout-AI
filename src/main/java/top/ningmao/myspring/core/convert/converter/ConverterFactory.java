package top.ningmao.myspring.core.convert.converter;


/**
 * 类型转换工厂接口，用于创建从源类型 S 到目标类型层次结构 R 的转换器
 * 
 * <p>当需要将一个源类型转换为目标类型层次结构中的多个不同类型时，使用此接口。
 * 该工厂可以根据具体的目标类型动态创建相应的转换器实例。</p>
 *
 * @param <S> 源类型（Source type）
 * @param <R> 目标类型层次结构的根类型（Root type of target type hierarchy）
 * @author NingMao
 * @since 2025-08-19
 */
public interface ConverterFactory<S, R> {

    /**
     * 根据指定的目标类型获取相应的转换器
     *
     * @param <T> 具体的目标类型，必须是 R 的子类型
     * @param targetType 目标类型的 Class 对象，不能为 null
     */
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
