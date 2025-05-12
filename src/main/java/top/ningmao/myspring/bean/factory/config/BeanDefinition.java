package top.ningmao.myspring.bean.factory.config;


import top.ningmao.myspring.bean.PropertyValues;

/**
 * 实例保存 bean 的信息，包括 class 类型、构造方法，构造方法参数、是否单例、是否为抽象 Bean 等等，此处简化为只包含 class 类型和 bean 属性
 *
 * @author ningmao
 * @since 2025-4-29
 */
public class BeanDefinition {
    
    private Class beanClass;
    
    private PropertyValues propertyValues;
    
    private String initMethodName;
    
    private String destroyMethodName;
    
    public BeanDefinition(Class beanClass) {
        this(beanClass, null);
    }
    
    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
    }
    
    public Class getBeanClass() {
        return beanClass;
    }
    
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
    
    public PropertyValues getPropertyValues() {
        return propertyValues;
    }
    
    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }
    
    public String getInitMethodName() {
        return initMethodName;
    }
    
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }
    
    public String getDestroyMethodName() {
        return destroyMethodName;
    }
    
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }
}
