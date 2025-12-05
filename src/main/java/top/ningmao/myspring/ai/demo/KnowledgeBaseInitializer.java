package top.ningmao.myspring.ai.demo;

import top.ningmao.myspring.ai.document.Document;
import top.ningmao.myspring.ai.document.MarkdownDocumentReader;
import top.ningmao.myspring.bean.factory.annotation.Autowired;
import top.ningmao.myspring.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 知识库初始化器
 * 负责加载 README 和包结构到向量库
 *
 * @author 宁猫
 * @since 2025-12-04
 */
@Component
public class KnowledgeBaseInitializer {

    @Autowired
    private AIConfiguration aiConfiguration;

    /**
     * 初始化知识库
     */
    public void initialize() {
        System.out.println("\n=== 开始初始化知识库 ===");
        
        List<Document> allDocuments = new ArrayList<>();
        
        // 1. 加载 README.MD（已自动切分）
        allDocuments.addAll(loadReadme());

        // 2. 加载包结构信息（tree 视图）
        allDocuments.addAll(loadPackageStructure());

        // 3. 加载教学文档（docs/*.md）
        allDocuments.addAll(loadTutorials());
        
        System.out.println(">>> 知识库文档总数: " + allDocuments.size() + " 个块");
        
        // 4. 向量化并存储
        System.out.println(">>> 开始向量化并存储（这可能需要一些时间...）");
        aiConfiguration.getVectorStore().add(allDocuments);
        
        System.out.println(" 知识库初始化完成！\n");
    }

    /**
     * 加载 README.MD
     */
    private List<Document> loadReadme() {
        System.out.println(">>> 加载 README.MD...");
        
        try {
            // 获取项目根目录的 README.MD
            String projectRoot = System.getProperty("user.dir");
            Path readmePath = Path.of(projectRoot, "README.MD");
            
            if (!readmePath.toFile().exists()) {
                System.err.println(" README.MD 不存在: " + readmePath);
                return List.of();
            }
            
            // 按二级标题分割，并自动进行段落切分
            // 大文档两步处理：先按标题分章节，再按段落切块
            MarkdownDocumentReader reader = MarkdownDocumentReader.builder()
                    .filePath(readmePath)
                    .splitByHeadingLevel(3)  // 按 ## 分割
                    .metadata("source", "README.MD")
                    .metadata("type", "documentation")
                    .build();
            
            List<Document> documents = reader.read();
            System.out.println("    加载并切分了 " + documents.size() + " 个文档块");
            
            return documents;
        } catch (Exception e) {
            System.err.println(" 加载 README 失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 加载教学文档（docs/*.md）
     */
    private List<Document> loadTutorials() {
        System.out.println(">>> 加载教学文档 (docs/*.md)...");

        try {
            String projectRoot = System.getProperty("/Users/didi/sprout");
            File docsDir = new File(projectRoot, "docs");

            if (!docsDir.exists() || !docsDir.isDirectory()) {
                System.out.println("    未找到 docs 目录，跳过教学文档加载");
                return List.of();
            }

            File[] files = docsDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".md"));

            if (files == null || files.length == 0) {
                System.out.println("    docs 目录下没有 .md 教程文档");
                return List.of();
            }

            List<Document> documents = new ArrayList<>();

            for (File file : files) {
                try {

                    MarkdownDocumentReader reader = MarkdownDocumentReader.builder()
                            .filePath(file.toPath())
                            .splitByHeadingLevel(2)
                            .metadata("source", "docs/" + file.getName())
                            .metadata("type", "tutorial")
                            .build();

                    documents.addAll(reader.read());
                } catch (Exception e) {
                    System.err.println("    加载教程文档失败: " + file.getName() + " - " + e.getMessage());
                }
            }

            System.out.println("    共加载教程文档 " + documents.size() + " 个块");
            return documents;

        } catch (Exception e) {
            System.err.println(" 教学文档加载失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 加载包结构信息
     */
    private List<Document> loadPackageStructure() {
        System.out.println(">>> 分析项目包结构...");
        
        try {

            
            String structure = "Project Structure(省略了无关的文件): myspring (Core & AI Assistant)\n" +
                    "\n" +
                    "[Module: AI & LLM Integration]\n" +
                    "ai/chat/advisor: Advisor.java, AdvisorChainExecutor.java, MessageChatMemoryAdvisor.java, QuestionAnswerAdvisor.java\n" +
                    "ai/chat/client: ChatClient.java, DefaultChatClient.java, AdvisorSpec.java, CallResponseSpec.java, StreamResponseSpec.java\n" +
                    "ai/chat/memory: ChatMemory.java, ChatMemoryRepository.java, InMemoryChatMemoryRepository.java, RedisChatMemoryRepository.java, MessageWindowChatMemory.java\n" +
                    "ai/chat/messages: Message.java (Interface), UserMessage.java, SystemMessage.java, AssistantMessage.java, ToolMessage.java\n" +
                    "ai/chat/model: ChatModel.java, StreamingChatModel.java, ChatResponse.java, Generation.java\n" +
                    "ai/chat/prompt: Prompt.java, ChatOptions.java, DeepSeekChatOptions.java, PromptTemplate.java\n" +
                    "ai/config: DeepSeekChatModelFactoryBean.java\n" +
                    "ai/deepseek: DeepSeekChatModel.java\n" +
                    "ai/document: Document.java, DocumentReader.java, TextSplitter.java, CharacterTextSplitter.java, MarkdownDocumentReader.java\n" +
                    "ai/embedding: EmbeddingModel.java, EmbeddingRequest.java, EmbeddingResponse.java\n" +
                    "ai/model/function: Tool.java, ToolCallback.java, ToolScanner.java\n" +
                    "ai/vectorstore: VectorStore.java, SimpleVectorStore.java, RedisVectorStore.java, VectorStoreRetriever.java\n" +
                    "\n" +
                    "[Module: Spring AOP (Aspect Oriented Programming)]\n" +
                    "aop: Advisor.java, Pointcut.java, TargetSource.java, ClassFilter.java, MethodMatcher.java\n" +
                    "aop/advice: BeforeAdvice.java, AfterAdvice.java, AroundAdvice.java, ThrowsAdvice.java\n" +
                    "aop/aspectj: AspectJExpressionPointcut.java\n" +
                    "aop/framework: AopProxy.java, CglibAopProxy.java, JdkDynamicAopProxy.java, ProxyFactory.java, ReflectiveMethodInvocation.java\n" +
                    "aop/framework/adapter: MethodBeforeAdviceInterceptor.java, MethodAfterAdviceInterceptor.java\n" +
                    "aop/framework/autoproxy: DefaultAdvisorAutoProxyCreator.java\n" +
                    "\n" +
                    "[Module: Spring IOC & Bean (Inversion of Control)]\n" +
                    "bean: BeansException.java, PropertyValues.java\n" +
                    "bean/factory: BeanFactory.java, FactoryBean.java, InitializingBean.java, DisposableBean.java, Aware.java\n" +
                    "bean/factory/annotation: Autowired.java, Qualifier.java, Value.java, AutowiredAnnotationBeanPostProcessor.java\n" +
                    "bean/factory/config: BeanDefinition.java, BeanPostProcessor.java, BeanFactoryPostProcessor.java, SingletonBeanRegistry.java\n" +
                    "bean/factory/support: DefaultListableBeanFactory.java, AbstractBeanFactory.java, BeanDefinitionReader.java, SimpleInstantiationStrategy.java\n" +
                    "bean/factory/xml: XmlBeanDefinitionReader.java\n" +
                    "\n" +
                    "[Module: Spring Context & Core]\n" +
                    "context: ApplicationContext.java, ConfigurableApplicationContext.java, ApplicationEvent.java, ApplicationListener.java\n" +
                    "context/annotation: ClassPathBeanDefinitionScanner.java\n" +
                    "context/event: ApplicationEventMulticaster.java, ContextRefreshedEvent.java\n" +
                    "context/support: ClassPathXmlApplicationContext.java, AbstractApplicationContext.java\n" +
                    "core/io: Resource.java, ResourceLoader.java, ClassPathResource.java, FileSystemResource.java\n" +
                    "core/convert: ConversionService.java, Converter.java, DefaultConversionService.java\n" +
                    "\n" +
                    "[Resources]\n" +
                    "configs: application.properties, ai-assistant-beans.xml";



            List<Document> documents = splitLongDocument(
                    structure.toString(),
                    "package-structure",
                    "structure",
                    1500 // 安全阈值，小于2048
            );


            System.out.println("    生成了包结构文档");
            
            return documents;
        } catch (Exception e) {
            System.err.println(" 生成包结构失败: " + e.getMessage());
            return List.of();
        }
    }
    private List<Document> splitLongDocument(String content, String source, String type, int maxChunkSize) {
        List<Document> chunks = new ArrayList<>();

        // 如果内容本身就不长，直接返回
        if (content.length() <= maxChunkSize) {
            chunks.add(Document.builder()
                    .content(content)
                    .metadata("source", source)
                    .metadata("type", type)
                    .metadata("chunk_index", 0)
                    .build());
            return chunks;
        }

        // 按行切分是更自然的方式，能保持文件树的逻辑结构
        String[] lines = content.split("\n");
        StringBuilder currentChunk = new StringBuilder();
        int chunkIndex = 0;

        for (String line : lines) {
            // 如果加上这行就超了，就把当前的块存起来，开始新块
            if (currentChunk.length() + line.length() + 1 > maxChunkSize && currentChunk.length() > 0) {
                chunks.add(Document.builder()
                        .content(currentChunk.toString())
                        .metadata("source", source)
                        .metadata("type", type)
                        .metadata("chunk_index", chunkIndex++)
                        .build());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(line).append("\n");
        }

        // 别忘了最后一个块
        if (currentChunk.length() > 0) {
            chunks.add(Document.builder()
                    .content(currentChunk.toString())
                    .metadata("source", source)
                    .metadata("type", type)
                    .metadata("chunk_index", chunkIndex)
                    .build());
        }

        return chunks;
    }
    private void buildDirectoryTree(File dir, String prefix, StringBuilder structure) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));

        List<File> children = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            if (name.equals(".git") || name.equals(".idea") || name.equals("target") || name.equals(".mvn")) {
                continue;
            }
            children.add(file);
        }

        for (int i = 0; i < children.size(); i++) {
            File child = children.get(i);
            boolean isLast = (i == children.size() - 1);

            structure.append(prefix);
            structure.append(isLast ? "└── " : "├── ");
            structure.append(child.getName());
            structure.append("\n");

            if (child.isDirectory()) {
                String childPrefix = prefix + (isLast ? "    " : "│   ");
                buildDirectoryTree(child, childPrefix, structure);
            }
        }
    }
}
