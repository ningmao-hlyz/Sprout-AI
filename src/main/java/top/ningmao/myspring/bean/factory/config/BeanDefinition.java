package top.ningmao.myspring.bean.factory.config;


import top.ningmao.myspring.bean.PropertyValues;

/**
 * 实例保存 bean 的信息，包括 class 类型、构造方法，构造方法参数、是否单例、是否为抽象 Bean 等等，此处简化为只包含 class 类型和 bean 属性
 *
 * @author ningmao
 * @since 2025-4-29
 */
public class BeanDefinition {
    
    // 单例作用域的常量标识
    public static String SCOPE_SINGLETON = "singleton";
    
    // 原型作用域的常量标识
    public static String SCOPE_PROTOTYPE = "prototype";
    
    // Bean 的 Class 类型，表示该 Bean 实际对应的类
    private Class beanClass;
    
    // Bean 的属性值集合，用于注入依赖属性
    private PropertyValues propertyValues;
    
    // Bean 的初始化方法名（可选）
    private String initMethodName;
    
    // Bean 的销毁方法名（可选）
    private String destroyMethodName;
    
    // Bean 的作用域，默认是单例 singleton
    private String scope = SCOPE_SINGLETON;
    
    // 是否是单例，默认 true
    private boolean singleton = true;
    
    // 是否是原型，默认 false
    private boolean prototype = false;

    // 是否是懒加载，默认 false
    private boolean lazyInit=false;

    
    public BeanDefinition(Class beanClass) {
        this(beanClass, null);
    }
    
    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
    }
    
    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }
    
    public boolean isSingleton() {
        return this.singleton;
    }
    
    public boolean isPrototype() {
        return this.prototype;
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

    public boolean isLazyInit(){
        return lazyInit;
    }

    public void setLazyInit(boolean b){
        lazyInit=b;
    }


}
