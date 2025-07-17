package top.ningmao.myspring.stereotype;

import java.lang.annotation.*;

/**
 * 用于标注一个类为组件（Bean），由容器自动检测并注册。
 *
 * @author NingMao
 * @since 2025-07-17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    /**
     * Bean 的名称（可选），默认为类名首字母小写。
     * @return 指定组件名称
     */
    String value() default "";
}
