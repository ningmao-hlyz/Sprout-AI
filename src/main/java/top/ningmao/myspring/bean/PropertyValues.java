package top.ningmao.myspring.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理 bean 属性
 *
 * @author ningmao
 * @since 2025-4-30
 */
public class PropertyValues {
    
    private final List<PropertyValue> propertyValueList = new ArrayList<>();
    
    public void addPropertyValue(PropertyValue propertyValue){
        propertyValueList.add(propertyValue);
    }
    
    public PropertyValue[] getPropertyValues(){
        return propertyValueList.toArray(new PropertyValue[0]);
    }
    
    public PropertyValue getPropertyValue(String propertyName){
        for(PropertyValue propertyValue : propertyValueList){
            if(propertyValue.getName().equals(propertyName)){
                return propertyValue;
            }
        }
        return null;
    }
}
