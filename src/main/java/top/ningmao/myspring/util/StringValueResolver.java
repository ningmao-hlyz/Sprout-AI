package top.ningmao.myspring.util;
/**
 * 解析（替换）字符串中的占位符或变量。
 *
 * @author NingMao
 * @since 2025-07-17
 */
public interface StringValueResolver {

    String resolveStringValue(String strVal);
}
