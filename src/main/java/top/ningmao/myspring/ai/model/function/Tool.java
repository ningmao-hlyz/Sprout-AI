package top.ningmao.myspring.ai.model.function;

import java.lang.annotation.*;


/**
 * Tool注解
 *
 * @author 宁猫
 * @since 2025-11-26 17:24:38
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tool {

    /**
     * 工具名称
     * 如果未提供，将使用方法名
     *
     * @return 工具名称
     */
    String name() default "";

    /**
     * 工具描述
     * 非常重要！AI 模型使用描述来理解何时以及如何调用工具
     *
     * @return 工具描述
     */
    String description();
}
