package top.ningmao.myspring.ioc;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;
import top.ningmao.myspring.core.io.DefaultResourceLoader;
import top.ningmao.myspring.core.io.FileSystemResource;
import top.ningmao.myspring.core.io.Resource;
import top.ningmao.myspring.core.io.UrlResource;

import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * 资源访问测试
 *
 * @author ningmao
 * @since 2025-5-6
 */
public class ResourceAndResourceLoaderTest {
    
    @Test
    public void testResourceLoader() throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        
        // 加载classpath下的资源
        Resource resource = resourceLoader.getResource("classpath:hello.txt");
        InputStream is = resource.getInputStream();
        String content = IoUtil.readUtf8(is);
        System.out.println(content);
        assertThat(content).isEqualTo("hello world");
        
        // 加载文件系统资源
        resource = resourceLoader.getResource("src/test/resources/hello.txt");
        assertThat(resource instanceof FileSystemResource).isTrue();
        is = resource.getInputStream();
        content = IoUtil.readUtf8(is);
        System.out.println(content);
        assertThat(content).isEqualTo("hello world");
        
        // 加载url资源
        resource = resourceLoader.getResource("https://gitee.com/ningmaoKing/imitation-spring");
        assertThat(resource instanceof UrlResource).isTrue();
        is = resource.getInputStream();
        content = IoUtil.readUtf8(is);
        System.out.println(content);
    }
}
