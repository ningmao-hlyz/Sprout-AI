package top.ningmao.myspring.core.convert.converter;
/**
 * 类型转换抽象接口，用于定义从源类型 S 到目标类型 T 的转换逻辑
 * 
 * <p>该接口是Spring框架中类型转换系统的核心组件，允许开发者自定义类型转换规则。
 * 通过实现此接口，可以将一种类型的对象转换为另一种类型的对象。</p>
 * 
 * <p>使用示例：</p>
 * <pre>
 * // 字符串到整数的转换器
 * public class StringToIntegerConverter implements Converter&lt;String, Integer&gt; {
 *     public Integer convert(String source) {
 *         return Integer.valueOf(source);
 *     }
 * }
 * </pre>
 *
 * @param <S> 源类型（Source type）
 * @param <T> 目标类型（Target type）
 * @author NingMao
 * @since 2025-08-19
 */
public interface Converter<S, T> {

    /**
     * 执行类型转换操作
     */
    T convert(S source);
}
