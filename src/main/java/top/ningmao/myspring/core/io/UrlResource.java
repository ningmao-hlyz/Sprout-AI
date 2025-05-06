package top.ningmao.myspring.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 加载 URL 形式的资源（包括本地文件或远程资源）
 *
 * @author ningmao
 * @since 2025-5-6
 */
public class UrlResource implements  Resource{
    
    private final URL url;
    
    public UrlResource(URL url) {
        this.url = url;
    }
    
    
    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            throw ex;
        }
    }
}
