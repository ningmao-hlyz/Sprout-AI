package top.ningmao.myspring.ai.demo;

import top.ningmao.myspring.ai.chat.advisor.QuestionAnswerAdvisor;
import top.ningmao.myspring.ai.chat.client.ChatClient;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.dashscope.DashScopeEmbeddingModel;
import top.ningmao.myspring.ai.deepseek.DeepSeekChatModel;
import top.ningmao.myspring.ai.embedding.EmbeddingModel;
import top.ningmao.myspring.ai.vectorstore.RedisVectorStore;
import top.ningmao.myspring.ai.vectorstore.VectorStore;
import top.ningmao.myspring.bean.factory.InitializingBean;
import top.ningmao.myspring.stereotype.Component;

/**
 * AI 应用配置组件
 * 使用 @Component 注解，由容器自动扫描并注册
 *
 * @author 宁猫
 * @since 2025-12-04
 */
@Component
public class AIConfiguration implements InitializingBean {

    private EmbeddingModel embeddingModel;
    private VectorStore vectorStore;
    private ChatModel chatModel;
    private QuestionAnswerAdvisor questionAnswerAdvisor;
    private ChatClient chatClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("\n=== 初始化 AI 组件 ===\n");
        
        // 1. 创建 EmbeddingModel（阿里百炼 DashScope）
        System.out.println(">>> 创建 EmbeddingModel（DashScope Text Embedding）");
        String dashscopeApiKey = System.getenv("DASHSCOPE_API_KEY");
        if (dashscopeApiKey == null || dashscopeApiKey.isBlank()) {
            throw new RuntimeException(
                "未设置 DASHSCOPE_API_KEY 环境变量！\n" +
                "请运行：export DASHSCOPE_API_KEY=your_api_key"
            );
        }
        // 使用阿里百炼的文本向量化服务（text-embedding-v2）
        this.embeddingModel = new DashScopeEmbeddingModel(dashscopeApiKey);
        
        // 2. 创建 VectorStore（依赖 EmbeddingModel）
        System.out.println(">>> 创建 VectorStore（Redis 持久化存储）");
        // DashScope text-embedding-v1 输出 1536 维向量
        this.vectorStore = new RedisVectorStore(
                embeddingModel, 
                "localhost", 
                6379, 
                "sprout-idx", 
                "doc:", 
                1536  // 匹配 DashScope 的向量维度
        );
        
        // 3. 创建 ChatModel（使用 DeepSeek-R1 推理模型）
        System.out.println(">>> 创建 ChatModel（DeepSeek-R1 Reasoner）");
        String apiKey = System.getenv("DEEPSEEK_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException(
                "未设置 DEEPSEEK_API_KEY 环境变量！\n" +
                "请运行：export DEEPSEEK_API_KEY=your_api_key"
            );
        }
        
        // 配置推理模型选项
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-chat")  // 使用推理模型，具有更强的逻辑推理能力
                .maxTokens(8192)  // 增加 token 配额，支持更详细的回答
                .temperature(0.3f)  // 降低温度，提高准确性和一致性
                .tools(ProjectLearningTools.class)
                .build();
        
        this.chatModel = new DeepSeekChatModel(apiKey, options);
        
        // 4. 创建 QuestionAnswerAdvisor（依赖 VectorStore）
        System.out.println(">>> 创建 QuestionAnswerAdvisor（依赖 VectorStore）");
        this.questionAnswerAdvisor = QuestionAnswerAdvisor.builder()
                .vectorStore(vectorStore)
                .topK(5)  // 增加检索数量，获取更多相关上下文
                .similarityThreshold(0.4)  // 降低阈值，包含更多潜在相关内容
                .build();
        
        // 5. 创建 ChatClient（依赖 ChatModel + QuestionAnswerAdvisor）
        System.out.println(">>> 创建 ChatClient（依赖 ChatModel + QuestionAnswerAdvisor）");
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(questionAnswerAdvisor)
                .defaultSystem("你是 **Sprout-AI 项目的智能架构导师**，一个基于 Java 编写的 AI 助手。\n" +
                        "Sprout-AI 是一个教学项目，旨在手写实现 Spring 核心（IoC/AOP）并集成 Spring AI 能力。\n\n" +
                        "## ️ 核心能力与工具策略\n" +
                        "你有权访问项目的实时代码和运行时状态。**请积极使用工具**来获取确切信息，严禁臆造代码或配置。\n" +
                        "1. **代码查阅**：当用户询问具体实现（如“BeanFactory怎么写的？”）时，**必须**先调用 `readFileContent` 读取源码，再基于源码讲解。\n" +
                        "2. **架构洞察**：当用户询问类关系（如“ChatModel有哪些实现？”）时，调用 `findBeansByType` 或 `listProjectFiles` 进行侦查。\n" +
                        "3. **运行时内省**：当用户询问当前状态（如“现在有哪些Bean？”），调用 `getAllBeanNames` 或 `getBeanDefinitionDetail`。\n\n" +
                        "##  学习路径引导\n" +
                        "你的任务不仅是答疑，还要根据[知识库]中的学习路径文档，引导用户循序渐进：\n" +
                        "- **初学者**：引导关注 IoC 容器实现（Bean定义、加载、注册）。\n" +
                        "- **进阶者**：引导关注 AOP 代理（JDK/CGLIB）、Bean 生命周期、循环依赖解决。\n" +
                        "- **高阶者**：引导关注 Spring AI 集成（ChatClient、RAG、Function Calling 实现）。\n" +
                        "*每次回答完问题后，请根据当前上下文，推荐 1-2 个下一步值得阅读的核心类文件或知识点。*\n\n" +
                        "##  回答规范\n" +
                        "1. **源码驱动 (Source-Code Driven)**\n" +
                        "   - 解释原理时，必须引用具体的类名和方法名（如 `doGetBean`, `refresh`）。\n" +
                        "   - 使用工具读取到的真实代码片段来佐证你的观点。\n" +
                        "2. **深度解析**\n" +
                        "   - 指出 Sprout-AI 的实现与 Spring 原生源码的**异同点**（简化了什么，保留了什么）。\n" +
                        "   - 剖析涉及的**设计模式**（如：模板方法模式在 `AbstractApplicationContext` 中的应用）。\n" +
                        "3. **结构清晰**\n" +
                        "   - 使用 Markdown 格式。对于复杂流程，使用 `Step 1 -> Step 2` 的形式描述，少用emoji。\n\n" +
                        "##  禁忌\n" +
                        "- 严禁捏造项目里不存在的功能。\n" +
                        "- 如果工具调用失败或知识库中没有相关信息，请诚实告知，并基于通用 Spring 原理解答（需标注）。")
                .build();
        
        System.out.println(" AI 组件初始化完成！\n");
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }

    public ChatClient getChatClient() {
        return chatClient;
    }
}
