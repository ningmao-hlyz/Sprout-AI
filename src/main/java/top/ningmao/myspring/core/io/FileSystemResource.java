package top.ningmao.myspring.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * 文件系统资源访问
 *
 * @author ningmao
 * @since 2025-5-6
 */
public class FileSystemResource implements Resource{
    private final String path;
    
    public FileSystemResource(String path) {
        this.path = path;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        try {
            Path path = new File(this.path).toPath();
            return Files.newInputStream(path);
        } catch (NoSuchFileException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
