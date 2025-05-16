package top.ningmao.myspring.context;

import java.util.EventObject;

/**
 * @author ningmao
 * @since 2025-5-15
 */
public abstract class ApplicationEvent extends EventObject {
    
    public ApplicationEvent(Object source){
        super(source);
    }
}
