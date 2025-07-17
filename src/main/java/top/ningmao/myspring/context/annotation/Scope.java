package top.ningmao.myspring.context.annotation;

import java.lang.annotation.*;

/**
 * 配置bean的作用域
 *
 * @author NingMao
 * @since 2025-07-17
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

    String value() default "singleton";
}
