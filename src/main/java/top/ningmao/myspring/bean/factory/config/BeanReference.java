package top.ningmao.myspring.bean.factory.config;
/**
 * 一个 bean 对另一个 bean 的引用
 *
 * @author ningmao
 * @since 2025-5-6
 */
public class BeanReference {
    
    private final String beanName;
    
    public BeanReference(String beanName)
    {
        this.beanName = beanName;
    }
    
    public String getBeanName()
    {
        return beanName;
    }
}
