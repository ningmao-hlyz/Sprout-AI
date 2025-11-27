package top.ningmao.myspring.ai.model.function;

import java.lang.annotation.*;


/**
 * 为工具方法的参数提供额外信息
 *
 * @author 宁猫
 * @since 2025-11-26 17:27:54
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToolParam {

    /**
     * 参数描述
     * 帮助 AI 模型理解参数的用途
     *
     * @return 参数描述
     */
    String description() default "";

    /**
     * 参数是否必需
     * 默认为 true（必需）
     *
     * @return 是否必需
     */
    boolean required() default true;
}
