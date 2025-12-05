package top.ningmao.myspring.ai.demo;

import top.ningmao.myspring.ai.model.function.Tool;
import top.ningmao.myspring.ai.model.function.ToolParam;
import top.ningmao.myspring.bean.factory.ConfigurableListableBeanFactory;
import top.ningmao.myspring.bean.factory.ListableBeanFactory;
import top.ningmao.myspring.bean.factory.config.BeanDefinition;
import top.ningmao.myspring.bean.factory.support.DefaultListableBeanFactory;
import top.ningmao.myspring.context.ApplicationContext;
import top.ningmao.myspring.context.ConfigurableApplicationContext;
import top.ningmao.myspring.context.support.AbstractApplicationContext;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * AI 学习助手专用工具类
 * 提供项目文件读取和 IoC 容器运行时状态查询功能
 */
public class ProjectLearningTools {

    private final String projectRoot;
    public static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
            "classpath:ai-assistant-beans.xml"
    );

    public ProjectLearningTools() {
        // 假设项目运行根目录即为项目根目录
        this.projectRoot = System.getProperty("user.dir");
    }

    // ================= 1. 静态代码分析能力 =================

    @Tool(description = "列出项目指定目录下的文件和文件夹结构，帮助理解包结构和代码组织方式。")
    public String listProjectFiles(
            @ToolParam(description = "相对于项目根目录的路径，例如 src/main/java/top/ningmao/myspring/ioc") String relativePath) {
        try {
            Path dir = Paths.get(projectRoot, relativePath);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                return "目录不存在: " + relativePath;
            }
            File[] files = dir.toFile().listFiles();
            if (files == null) return "空目录";

            return Arrays.stream(files)
                    .map(f -> (f.isDirectory() ? "[DIR] " : "[FILE] ") + f.getName())
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "列出文件失败: " + e.getMessage();
        }
    }

    @Tool(description = "读取指定文件的源代码内容（支持 Java, XML, Properties 等），用于解释类的具体实现原理。")
    public String readFileContent(
            @ToolParam(description = "文件的相对路径，例如 src/main/java/top/ningmao/myspring/bean/factory/BeanFactory.java,其中src/main/java/top/ningmao/myspring是固定的") String relativePath) {
        try {
            Path path = Paths.get(projectRoot, relativePath).normalize();
            if (!path.startsWith(Paths.get(projectRoot))) {
                return "拒绝访问: 禁止读取项目根目录以外的文件。";
            }

            if (!Files.exists(path)) {
                return "文件未找到: " + relativePath;
            }

            String content = Files.readString(path);
            if (content.length() > 5000) {
                return content.substring(0, 5000) + "\n... (内容过长已截断)";
            }
            return content;
        } catch (IOException e) {
            return "读取文件失败: " + e.getMessage();
        }
    }

    // ================= 2. 运行时容器内省能力 =================

    @Tool(description = "获取当前 IoC 容器中所有已注册 Bean 的名称列表。")
    public String getAllBeanNames() {
        if (applicationContext != null) {
            String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
            System.out.println("beanDefinitionNames = " + Arrays.toString(beanDefinitionNames));
            return "已注册的 Bean: " + Arrays.toString(beanDefinitionNames);
        }
        return "当前 ApplicationContext 不支持查看 Bean 列表。";
    }

    @Tool(description = "获取指定 Bean 的详细定义信息，包括它的完整类名、作用域（单例/原型）、懒加载状态以及属性配置。")
    public String getBeanDefinitionDetail(
            @ToolParam(description = "Bean 的名称") String beanName) {
        if (applicationContext instanceof AbstractApplicationContext) {
            ConfigurableListableBeanFactory beanFactory = ((AbstractApplicationContext) applicationContext).getBeanFactory();

            if (beanFactory instanceof DefaultListableBeanFactory) {
                DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
                try {
                    BeanDefinition bd = factory.getBeanDefinition(beanName);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bean 名称: ").append(beanName).append("\n");

                    Class<?> beanClass = bd.getBeanClass();
                    sb.append("类名: ").append(beanClass != null ? beanClass.getName() : "null").append("\n");
                    sb.append("作用域: ").append(bd.isSingleton() ? "Singleton (单例)" : "Prototype (原型)").append("\n");
                    sb.append("懒加载: ").append(bd.isLazyInit()).append("\n");

                    if (bd.getPropertyValues() != null && bd.getPropertyValues().getPropertyValues() != null) {
                        sb.append("配置属性数: ").append(bd.getPropertyValues().getPropertyValues().length);
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return "未找到 Bean 定义: " + beanName;
                }
            }
        }
        return "当前容器不支持查看 Bean 定义详情。";
    }

    @Tool(description = "检查指定的 Bean 是否被 AOP 代理（CGLIB 或 JDK 动态代理）。")
    public String isBeanProxied(
            @ToolParam(description = "Bean 的名称") String beanName) {
        try {
            Object bean = applicationContext.getBean(beanName);
            if (bean == null) return "未找到 Bean 实例: " + beanName;

            String className = bean.getClass().getName();
            boolean isCglib = className.contains("CGLIB");
            boolean isJdk = java.lang.reflect.Proxy.isProxyClass(bean.getClass());

            if (isCglib) return "是，" + beanName + " 被 CGLIB 代理了。";
            if (isJdk) return "是，" + beanName + " 被 JDK 动态代理了。";
            return "否，" + beanName + " 是原生对象，未被代理。";
        } catch (Exception e) {
            return "检查 Bean 失败: " + e.getMessage();
        }
    }

    @Tool(description = "查找所有实现了指定接口或继承了指定类的 Bean。支持输入简短类名（如 'ChatModel'）或全限定类名。")
    public String findBeansByType(
            @ToolParam(description = "接口名或类名，例如 'ChatModel' 或 'top.ningmao.myspring.service.HelloService'") String typeName) {

        ConfigurableListableBeanFactory beanFactory = ((AbstractApplicationContext) applicationContext).getBeanFactory();

        try {
            Class<?> targetType = resolveClass(typeName);
            if (targetType == null) {
                return "未找到类型: " + typeName + "。请检查拼写或尝试使用全限定类名。";
            }

            StringBuilder result = new StringBuilder();
            result.append("正在搜索类型为 ").append(targetType.getName()).append(" 的 Bean...\n");
            result.append("--------------------------------------------------\n");

            String[] beanNames = beanFactory.getBeanDefinitionNames();
            int count = 0;

            for (String beanName : beanNames) {
                try {
                    BeanDefinition bd = beanFactory.getBeanDefinition(beanName);

                    // 直接获取 Class 对象
                    Class<?> beanClass = bd.getBeanClass();

                    if (beanClass == null) continue;

                    // 判定是否匹配
                    if (targetType.isAssignableFrom(beanClass)) {
                        count++;
                        result.append(String.format("发现 Bean: [%s]\n", beanName));
                        result.append(String.format("  - 实际类: %s\n", beanClass.getName()));

                        if (bd.isSingleton()) {
                            try {
                                Object instance = applicationContext.getBean(beanName);
                                if (instance.getClass().getName().contains("$$EnhancerByCGLIB") ||
                                        java.lang.reflect.Proxy.isProxyClass(instance.getClass())) {
                                    result.append("  - 状态: 已代理 (AOP生效)\n");
                                } else {
                                    result.append("  - 状态: 原生实例\n");
                                }
                            } catch (Exception ignored) {
                                result.append("  - 状态: 无法获取实例检查\n");
                            }
                        }
                        result.append("\n");
                    }
                } catch (Exception e) {
                    // 忽略单个 Bean 的检查错误
                }
            }

            if (count == 0) {
                return "未找到类型为 " + targetType.getSimpleName() + " 的 Bean。";
            }

            return result.toString();

        } catch (Exception e) {
            return "执行类型查找失败: " + e.getMessage();
        }
    }

    // ========================== 私有辅助方法 ==========================

    private Class<?> resolveClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ignored) {
        }

        String basePackage = "top.ningmao.myspring";
        try {
            return scanPackageForClass(basePackage, name);
        } catch (Exception e) {
            return null;
        }
    }

    private Class<?> scanPackageForClass(String packageName, String simpleClassName) throws IOException, ClassNotFoundException {
        String path = packageName.replace('.', '/');
        java.net.URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

        if (resource == null) return null;

        File directory = new File(resource.getFile());
        if (!directory.exists()) return null;

        return recursiveFindClass(directory, packageName, simpleClassName);
    }

    private Class<?> recursiveFindClass(File directory, String packageName, String targetSimpleName) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file.isDirectory()) {
                Class<?> found = recursiveFindClass(file, packageName + "." + file.getName(), targetSimpleName);
                if (found != null) return found;
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                if (className.equals(targetSimpleName)) {
                    return Class.forName(packageName + "." + className);
                }
            }
        }
        return null;
    }
}