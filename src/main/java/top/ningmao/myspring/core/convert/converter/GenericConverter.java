package top.ningmao.myspring.core.convert.converter;

import java.util.Set;

/**
 * 通用类型转换器接口，提供最灵活的类型转换能力
 *
 * @author NingMao
 * @since 2025-08-19
 */
public interface GenericConverter {

    /**
     * 获取此转换器支持的所有源类型到目标类型的转换对
     */
    Set<ConvertiblePair> getConvertibleTypes();

    /**
     * 执行类型转换操作
     */
    Object convert(Object source, Class sourceType, Class targetType);

    /**
     * 类型转换对，表示从源类型到目标类型的转换关系
     */
    public static final class ConvertiblePair {

        /**
         * 源类型
         */
        private final Class<?> sourceType;

        /**
         * 目标类型
         */
        private final Class<?> targetType;

        /**
         * 构造一个新的转换对
         *
         * @param sourceType 源类型，不能为null
         * @param targetType 目标类型，不能为null
         * @throws IllegalArgumentException 如果任一参数为null
         */
        public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        /**
         * 获取源类型
         *
         * @return 源类型的Class对象
         */
        public Class<?> getSourceType() {
            return this.sourceType;
        }

        /**
         * 获取目标类型
         *
         * @return 目标类型的Class对象
         */
        public Class<?> getTargetType() {
            return this.targetType;
        }

        /**
         * 比较两个ConvertiblePair对象是否相等
         * 
         * <p>当且仅当两个对象的源类型和目标类型都相等时，
         * 这两个ConvertiblePair对象才被认为是相等的。</p>
         *
         * @param obj 要比较的对象
         * @return 如果对象相等返回true，否则返回false
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != GenericConverter.ConvertiblePair.class) {
                return false;
            }
           GenericConverter.ConvertiblePair other = (ConvertiblePair) obj;
            return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);

        }

        /**
         * 计算ConvertiblePair对象的哈希码
         * 
         * <p>哈希码基于源类型和目标类型的哈希码计算，
         * 确保相等的对象具有相同的哈希码。</p>
         *
         * @return 对象的哈希码
         */
        @Override
        public int hashCode() {
            return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
        }
    }
}
