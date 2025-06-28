package top.ningmao.myspring.bean;
/**
 * bean 属性信息
 *
 * @author ningmao
 * @since 2025-4-30
 */
public class PropertyValue {
    
    private final String name;
    
    private final Object value;
    
    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public Object getValue() {
        return value;
    }
}
