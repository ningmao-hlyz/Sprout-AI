package top.ningmao.myspring.core.io;
/**
 * 资源加载器接口
 *
 * @author ningmao
 * @since 2025-5-6
 */
public interface ResourceLoader {
    
    Resource getResource(String location);
}
