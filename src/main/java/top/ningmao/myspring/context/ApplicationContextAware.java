package top.ningmao.myspring.context;

import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.Aware;

/**
 * 实现该接口，能感知所属 ApplicationContext
 *
 * @author ningmao
 * @since 2025-5-13
 */
public interface ApplicationContextAware  extends Aware {
    
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
