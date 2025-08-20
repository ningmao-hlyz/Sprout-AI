package top.ningmao.myspring.common;

import top.ningmao.myspring.core.convert.converter.Converter;

/**
 * @author NingMao
 * @since 2025-08-20
 */
public class StringToIntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        return Integer.valueOf(source);
    }
}
