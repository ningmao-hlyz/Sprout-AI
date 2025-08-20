package top.ningmao.myspring.common;


import top.ningmao.myspring.core.convert.converter.GenericConverter;

import java.util.Collections;
import java.util.Set;

/**
 * @author NingMao
 * @since 2025-08-20
 */
public class StringToBooleanConverter implements GenericConverter {
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Boolean.class));
    }

    @Override
    public Object convert(Object source, Class sourceType, Class targetType) {
        return Boolean.valueOf((String) source);
    }
}

