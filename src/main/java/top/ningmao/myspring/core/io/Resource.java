package top.ningmao.myspring.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源的抽象和访问接口
 *
 * @author ningmao
 * @since 2025-5-6
 */
public interface Resource {
    
    InputStream getInputStream() throws IOException;
}
