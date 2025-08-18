package top.ningmao.myspring.bean.factory.annotation;

import java.lang.annotation.*;

/**
 * 为注入bean时指定bean名称
 *
 * @author NingMao
 * @since 2025-08-19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Inherited
@Documented
public @interface Qualifier {

    String value() default "";

}