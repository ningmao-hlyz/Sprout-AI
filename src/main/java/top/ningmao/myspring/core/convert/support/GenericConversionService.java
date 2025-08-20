package top.ningmao.myspring.core.convert.support;

import top.ningmao.myspring.core.convert.ConversionService;
import top.ningmao.myspring.core.convert.converter.Converter;
import top.ningmao.myspring.core.convert.converter.ConverterFactory;
import top.ningmao.myspring.core.convert.converter.ConverterRegistry;
import top.ningmao.myspring.core.convert.converter.GenericConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 通用的类型转换服务实现类，是 Spring 类型转换体系的核心。
 * 它实现了 ConversionService (用于执行转换) 和 ConverterRegistry (用于注册转换器) 两个接口。
 *
 * @author NingMao
 * @since 2025-08-19
 */
public class GenericConversionService implements ConversionService, ConverterRegistry {


    /**
     * 存储所有转换器的容器。
     * Key: ConvertiblePair，代表一个 "源类型 -> 目标类型" 的转换对。
     * Value: GenericConverter，所有类型的转换器最终都会被适配成 GenericConverter 存储。
     */
    private Map<GenericConverter.ConvertiblePair, GenericConverter> converters = new HashMap<>();

    /**
     * 判断是否存在可以将 sourceType 转换为 targetType 的转换器。
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 如果可以转换，返回 true；否则返回 false。
     */
    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        GenericConverter converter = getConverter(sourceType, targetType);
        return converter != null;
    }

    /**
     * 将给定的 source 对象转换为指定的目标类型 targetType。
     *
     * @param source     要转换的源对象
     * @param targetType 要转换的目标类型
     * @return 转换后的对象
     * @throws ClassCastException 如果转换失败
     */
    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        Class<?> sourceType = source.getClass();
        GenericConverter converter = getConverter(sourceType, targetType);
        // 调用找到的转换器的 convert 方法执行实际的转换逻辑
        return (T) converter.convert(source, sourceType, targetType);
    }

    /**
     * 注册一个标准的 Converter (只能处理一种精确的类型转换)。
     *
     * @param converter 实现了 Converter<S, T> 接口的转换器实例。
     */
    @Override
    public void addConverter(Converter<?, ?> converter) {
        // 1. 通过反射获取 Converter 的 <S, T> 泛型类型信息
        GenericConverter.ConvertiblePair typeInfo = getRequiredTypeInfo(converter);
        // 2. 将标准的 Converter 适配成一个通用的 GenericConverter
        ConverterAdapter converterAdapter = new ConverterAdapter(typeInfo, converter);
        // 3. 将适配后的转换器注册到 converters 容器中
        for (GenericConverter.ConvertiblePair convertibleType : converterAdapter.getConvertibleTypes()) {
            converters.put(convertibleType, converterAdapter);
        }
    }

    /**
     * 注册一个 ConverterFactory (能处理一类转换，例如 String -> Enum)。
     *
     * @param converterFactory 实现了 ConverterFactory<S, R> 接口的转换器工厂实例。
     */
    @Override
    public void addConverterFactory(ConverterFactory<?, ?> converterFactory) {
        // 1. 通过反射获取 ConverterFactory 的 <S, R> 泛型类型信息
        GenericConverter.ConvertiblePair typeInfo = getRequiredTypeInfo(converterFactory);
        // 2. 将 ConverterFactory 适配成一个通用的 GenericConverter
        ConverterFactoryAdapter converterFactoryAdapter = new ConverterFactoryAdapter(typeInfo, converterFactory);
        // 3. 将适配后的转换器注册到 converters 容器中
        for (GenericConverter.ConvertiblePair convertibleType : converterFactoryAdapter.getConvertibleTypes()) {
            converters.put(convertibleType, converterFactoryAdapter);
        }
    }

    /**
     * 注册一个通用的 GenericConverter (功能最强大，可以处理多种复杂的类型转换)。
     *
     * @param converter 实现了 GenericConverter 接口的转换器实例。
     */
    @Override
    public void addConverter(GenericConverter converter) {
        // GenericConverter 本身支持多种转换对，直接遍历并注册
        for (GenericConverter.ConvertiblePair convertibleType : converter.getConvertibleTypes()) {
            converters.put(convertibleType, converter);
        }
    }

    /**
     * 通过反射从实现了 Converter 或 ConverterFactory 接口的对象中提取其泛型参数类型。
     * 例如，对于 StringToIntegerConverter implements Converter<String, Integer>，
     * 此方法将返回一个包含 String.class 和 Integer.class 的 ConvertiblePair。
     *
     * @param object 转换器或转换器工厂实例
     * @return 包含源类型和目标类型的 ConvertiblePair
     */
    private GenericConverter.ConvertiblePair getRequiredTypeInfo(Object object) {
        // 获取该对象实现的接口
        Type[] types = object.getClass().getGenericInterfaces();
        // 假设第一个接口就是我们需要的 (Converter or ConverterFactory)
        ParameterizedType parameterized = (ParameterizedType) types[0];
        // 获取接口的泛型参数数组，例如 <String, Integer>
        Type[] actualTypeArguments = parameterized.getActualTypeArguments();
        // 第一个泛型是源类型
        Class sourceType = (Class) actualTypeArguments[0];
        // 第二个泛型是目标类型
        Class targetType = (Class) actualTypeArguments[1];
        return new GenericConverter.ConvertiblePair(sourceType, targetType);
    }

    /**
     * 根据源类型和目标类型查找合适的转换器。
     * 这个方法是 Spring 类型转换查找逻辑的核心，它不仅会精确匹配，还会考虑继承体系。
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 合适的 GenericConverter，如果找不到则返回 null。
     */
    protected GenericConverter getConverter(Class<?> sourceType, Class<?> targetType) {
        // 1. 获取源类型的所有父类和接口层级
        List<Class<?>> sourceCandidates = getClassHierarchy(sourceType);
        // 2. 获取目标类型的所有父类和接口层级
        List<Class<?>> targetCandidates = getClassHierarchy(targetType);

        // 3. 遍历所有可能的 "源类型 -> 目标类型" 组合，寻找注册过的转换器
        for (Class<?> sourceCandidate : sourceCandidates) {
            for (Class<?> targetCandidate : targetCandidates) {
                GenericConverter.ConvertiblePair convertiblePair = new GenericConverter.ConvertiblePair(sourceCandidate, targetCandidate);
                GenericConverter converter = converters.get(convertiblePair);
                // 一旦找到，立即返回
                if (converter != null) {
                    return converter;
                }
            }
        }
        // 如果遍历完所有组合都找不到，则返回 null
        return null;
    }

    /**
     * 获取一个类的完整继承层级结构，从当前类开始，一直到 Object.class。
     *
     * @param clazz 待分析的类
     * @return 一个包含所有父类的列表
     */
    private List<Class<?>> getClassHierarchy(Class<?> clazz) {
        List<Class<?>> hierarchy = new ArrayList<>();
        while (clazz != null) {
            hierarchy.add(clazz);
            clazz = clazz.getSuperclass(); // 继续向上查找父类
        }
        return hierarchy;
    }

    /**
     * 内部适配器类，用于将标准的 Converter 包装成 GenericConverter。
     * 这是适配器模式的典型应用，目的是统一接口，方便管理。
     */
    private final class ConverterAdapter implements GenericConverter {

        private final ConvertiblePair typeInfo;
        private final Converter<Object, Object> converter;

        public ConverterAdapter(ConvertiblePair typeInfo, Converter<?, ?> converter) {
            this.typeInfo = typeInfo;
            // 将具体的 Converter 强制转换为通用的 Converter<Object, Object> 以便统一处理
            this.converter = (Converter<Object, Object>) converter;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            // Converter 只支持一种精确的转换
            return Collections.singleton(typeInfo);
        }

        @Override
        public Object convert(Object source, Class sourceType, Class targetType) {
            // 实际调用原始 Converter 的 convert 方法
            return converter.convert(source);
        }
    }

    /**
     * 内部适配器类，用于将 ConverterFactory 包装成 GenericConverter。
     */
    private final class ConverterFactoryAdapter implements GenericConverter {

        private final ConvertiblePair typeInfo;
        private final ConverterFactory<Object, Object> converterFactory;

        public ConverterFactoryAdapter(ConvertiblePair typeInfo, ConverterFactory<?, ?> converterFactory) {
            this.typeInfo = typeInfo;
            // 将具体的 ConverterFactory 强制转换为通用的类型
            this.converterFactory = (ConverterFactory<Object, Object>) converterFactory;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            // ConverterFactory 支持 "源类型 -> 目标类型及其子类" 的转换
            return Collections.singleton(typeInfo);
        }

        @Override
        public Object convert(Object source, Class sourceType, Class targetType) {
            // 1. 首先从工厂中根据具体的目标类型 (targetType) 获取一个特定的 Converter
            // 2. 然后调用该 Converter 的 convert 方法执行转换
            return converterFactory.getConverter(targetType).convert(source);
        }
    }
}
