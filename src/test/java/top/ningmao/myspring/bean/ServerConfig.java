package top.ningmao.myspring.bean;

import org.springframework.beans.factory.annotation.Value;
import top.ningmao.myspring.stereotype.Component;

/**
 * @author NingMao
 * @since 2025-07-17
 */
@Component
public class ServerConfig {

    @Value("${host}")
    private String url;

    @Value("${base.${env}}")
    private String path;

    @Value("${region.location}")
    private String location;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
