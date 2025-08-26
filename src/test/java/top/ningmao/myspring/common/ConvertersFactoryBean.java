package top.ningmao.myspring.common;

import top.ningmao.myspring.bean.factory.FactoryBean;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author NingMao
 * @since 2025-08-20
 */
public class ConvertersFactoryBean implements FactoryBean<Set<?>> {
    @Override
    public Set<?> getObject() throws Exception {
        HashSet<Object> converters = new HashSet<>();
        StringToLocalDateConverter stringToLocalDateConverter = new StringToLocalDateConverter("yyyy-MM-dd");
        converters.add(stringToLocalDateConverter);
        return converters;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
