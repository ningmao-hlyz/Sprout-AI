package top.ningmao.myspring.ai.demo;

import com.sun.net.httpserver.HttpServer;
import top.ningmao.myspring.bean.BeansException;
import top.ningmao.myspring.context.ApplicationContext;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * AI 小助手主应用
 * <p>
 * 演示项目能力：
 * 1. @Component 注解 + XML component-scan（自动 Bean 扫描）
 * 2. InitializingBean 生命周期管理
 * 3. RAG 检索增强生成
 * 4. Markdown 文档解析（自动两阶段切分）
 * 5. 向量数据库
 * 6. 简单 Web 服务
 *
 * @author 宁猫
 * @since 2025-12-04
 */
public class AIAssistantApplication {

    private static final int PORT = 8080;

    private static final ApplicationContext context = ProjectLearningTools.applicationContext;

    public static void main(String[] args) throws Exception {
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║           AI 小助手 - Sprout-AI  Demo                  ║");
        System.out.println("║                                                              ║");
        System.out.println("║   展示：@Component + IoC + RAG + 向量数据库 + 文档解析       ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // ===== 1. 检查环境变量 =====
        String apiKey = System.getenv("DEEPSEEK_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("\n 错误：未设置 DEEPSEEK_API_KEY 环境变量！");
            System.err.println("请运行：export DEEPSEEK_API_KEY=your_api_key");
            System.err.println("然后重新启动应用\n");
            System.exit(1);
        }

        // ===== 2. 加载 XML 配置，创建 ApplicationContext =====
        System.out.println("=== 步骤1：加载 XML 配置并扫描 @Component ===\n");
        System.out.println(">>> 读取 classpath:ai-assistant-beans.xml");
        System.out.println(">>> 扫描 top.ningmao.myspring.ai.demo 包");
        System.out.println(">>> 自动创建和注入 Bean...\n");
        

        System.out.println(" ApplicationContext 创建完成！");
        
        // ===== 3. 从容器获取 Bean =====
        System.out.println("\n=== 步骤2：从容器获取 Bean ===\n");
        KnowledgeBaseInitializer initializer = context.getBean("knowledgeBaseInitializer", KnowledgeBaseInitializer.class);
        ChatController chatController = context.getBean("chatController", ChatController.class);
        
        System.out.println(">>> 已获取 knowledgeBaseInitializer");
        System.out.println(">>> 已获取 chatController");
        
        // ===== 4. 初始化知识库 =====
        initializer.initialize();
        
        // ===== 5. 启动 Web 服务器 =====
        System.out.println("\n=== 步骤3：启动 Web 服务器 ===\n");
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // 注册 API 路由
        server.createContext("/api/chat", chatController);
        server.createContext("/api/health", chatController);
        
        // 注册静态文件路由
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            
            // 默认返回 index.html
            if ("/".equals(path)) {
                path = "/index.html";
            }
            
            // 读取静态文件
            try {
                String projectRoot = System.getProperty("user.dir");
                Path filePath = Path.of(projectRoot, "src/main/resources/static" + path);
                
                if (Files.exists(filePath)) {
                    byte[] bytes = Files.readAllBytes(filePath);
                    
                    // 设置 Content-Type
                    String contentType = "text/html";
                    if (path.endsWith(".css")) {
                        contentType = "text/css";
                    } else if (path.endsWith(".js")) {
                        contentType = "application/javascript";
                    }
                    
                    exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } else {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                String response = "500 Internal Server Error";
                exchange.sendResponseHeaders(500, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        });
        
        server.setExecutor(null); // 使用默认执行器
        server.start();
        
        System.out.println(" Web 服务器启动成功！\n");
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║     🚀 访问地址：http://localhost:" + PORT + "                     ║");
        System.out.println("║                                                              ║");
        System.out.println("║     可以开始向 AI 小助手提问关于项目的任何问题！             ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("\n>>> 按 Ctrl+C 停止服务器\n");
    }
}
