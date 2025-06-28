package top.ningmao.myspring.bean;

/**
 * bean 获取异常
 *
 * @author ningmao
 * @since 2025-4-29
 */

public class BeansException extends RuntimeException {
    public BeansException(String message) {
        super(message);
    }
    
    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
