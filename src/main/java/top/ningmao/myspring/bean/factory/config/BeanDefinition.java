package top.ningmao.myspring.bean.factory.config;
/**
 * 实例保存 bean 的信息，包括 class 类型、构造方法，构造方法参数、是否单例、是否为抽象 Bean 等等，此处简化为只包含 class 类型
 *
 * @author ningmao
 * @since 2025-4-29
 */
public class BeanDefinition {
    
    private Class beanClass;
    
    public BeanDefinition(Class beanClass){
        this.beanClass = beanClass;
    }
    
    public Class getBeanClass() {
        return beanClass;
    }
    
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
}
