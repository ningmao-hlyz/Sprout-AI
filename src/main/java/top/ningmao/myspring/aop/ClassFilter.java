package top.ningmao.myspring.aop;

/**
 * 定义类匹配规则
 *
 * @author NingMao
 * @since 2025-06-27
 */
public interface ClassFilter {

    /**
     * 判断类是否匹配
     *
     * @param clazz 类
     * @return 是否匹配
     */
    boolean matches(Class<?> clazz);
}
