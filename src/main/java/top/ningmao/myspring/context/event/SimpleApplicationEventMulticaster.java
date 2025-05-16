package top.ningmao.myspring.context.event;


import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.bean.factory.BeanFactory;
import top.ningmao.myspring.context.ApplicationEvent;
import top.ningmao.myspring.context.ApplicationListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author ningmao
 * @since 2025-5-16
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster{
    
    
    @Override
    public void multicastEvent(ApplicationEvent event) {
        for (ApplicationListener<ApplicationEvent> applicationListener : applicationListeners) {
            if (supportsEvent(applicationListener, event)) {
                applicationListener.onApplicationEvent(event);
            }
        }
    }
    
    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }
    
    /**
     * 判断一个监听器是否对该事件感兴趣（是否支持处理这个事件）
     *
     * @param applicationListener 监听器（实现了 ApplicationListener 接口）
     * @param event               当前要发布的事件
     * @return                    是否支持（true 表示监听器支持这个事件类型）
     */
    protected boolean supportsEvent(ApplicationListener<ApplicationEvent> applicationListener, ApplicationEvent event){
        // 获取 applicationListener 实现的第一个接口的泛型信息
        // 一般是 ApplicationListener<某个事件>
        Type type = applicationListener.getClass().getGenericInterfaces()[0];
        
        // 拿到这个接口的第一个泛型参数，也就是事件类型
        // 比如：ApplicationListener<MyEvent>，就拿到 MyEvent 这个类
        Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        
        // 获取泛型参数对应的全类名字符串
        String className = actualTypeArgument.getTypeName();
        
        Class<?> eventClassName;
        try {
            // 通过反射把类名转换成 Class 对象
            eventClassName = Class.forName(className);
        } catch (ClassNotFoundException e) {
            // 如果类名搞错了，抛出自定义异常
            throw new BeansException("wrong event class name: " + className);
        }
        
        // 判断事件 event 的类型是否是 eventClassName 的子类或本身
        // 换句话说，就是判断监听器声明的事件类型（eventClassName）是否能接收当前 event
        return eventClassName.isAssignableFrom(event.getClass());
        
        
    }
}
