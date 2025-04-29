package top.ningmao.myspring.bean.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 最简单的 bean 容器
 *
 * @author ningmao
 * @since 2025-4-29
 */
public class BeanFactory {
    
    
    private Map<String,Object> beanMap = new HashMap<>();
    
    public void registerBean(String name,Object bean){
        beanMap.put(name,bean);
    }
    
    public Object getBean(String name){
        return beanMap.get(name);
    }
}
