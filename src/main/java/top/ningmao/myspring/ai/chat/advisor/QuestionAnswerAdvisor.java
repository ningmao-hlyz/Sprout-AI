package top.ningmao.myspring.ai.chat.advisor;

import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.ai.document.Document;
import top.ningmao.myspring.ai.vectorstore.SearchRequest;
import top.ningmao.myspring.ai.vectorstore.VectorStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * QuestionAnswerAdvisor - RAG 检索增强 Advisor
 * <p>
 * 功能：
 * 1. 在请求前（adviseRequest）：从 VectorStore 检索相关文档
 * 2. 将检索到的文档注入到 System Message 作为上下文
 * 3. 将检索结果存入 ChatResponse metadata，便于后续评估
 * <p>
 *
 * @author 宁猫
 * @since 2025-12-04
 */
public class QuestionAnswerAdvisor implements Advisor {

    /**
     * ChatResponse metadata 中存储检索文档的 key
     */
    public static final String RETRIEVED_DOCUMENTS = "question_answer_retrieved_documents";

    /**
     * 默认的 System Message 模板
     * 参考 Spring AI 的实现，但改为中文
     */
    private static final String DEFAULT_USER_TEXT_ADVISE = "\n" +
            "以下是上下文信息，由 --------------------- 包围：\n" +
            "\n" +
            "---------------------\n" +
            "{question_answer_context}\n" +
            "---------------------\n" +
            "\n" +
            "请根据上述上下文信息和对话历史（而非你的先验知识）来回答用户的问题。\n" +
            "如果上下文中没有相关信息，请明确告知用户你无法回答该问题。\n";

    private static final String DEFAULT_NAME = "QuestionAnswerAdvisor";
    private static final int DEFAULT_ORDER = 0;
    private static final int DEFAULT_TOP_K = 5;
    private static final double DEFAULT_SIMILARITY_THRESHOLD = 0.7;

    private final VectorStore vectorStore;
    private final String userTextAdvise;
    private final int topK;
    private final double similarityThreshold;
    private final String name;
    private final int order;

    /**
     * 构造函数（私有，使用 Builder）
     */
    private QuestionAnswerAdvisor(Builder builder) {
        this.vectorStore = builder.vectorStore;
        this.userTextAdvise = builder.userTextAdvise;
        this.topK = builder.topK;
        this.similarityThreshold = builder.similarityThreshold;
        this.name = builder.name;
        this.order = builder.order;
    }

    /**
     * 创建 Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Prompt adviseRequest(Prompt prompt, Map<String, Object> params) {
        // 1. 提取用户问题
        String userQuestion = extractUserQuestion(prompt);
        if (userQuestion == null || userQuestion.isBlank()) {
            return prompt;
        }

        // 2. 向量检索相关文档
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(userQuestion)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .build()
        );

        // 3. 构建上下文文本
        String context = buildContext(relevantDocs);

        // 4. 将检索到的文档存入 params，便于在 adviseResponse 中访问
        // params 由 ChatClient 保证不为 null
        params.put(RETRIEVED_DOCUMENTS, relevantDocs);

        // 5. 构建增强的 System Message
        String enhancedSystemMessage = userTextAdvise.replace("{question_answer_context}", context);

        // 6. 创建新的消息列表（添加 System Message）
        List<Message> newMessages = new ArrayList<>();
        newMessages.add(new SystemMessage(enhancedSystemMessage));
        newMessages.addAll(prompt.getMessages());

        // 7. 创建新的 Prompt
        return new Prompt(newMessages, prompt.getOptions());
    }

    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> params) {
        // 将检索到的文档添加到 ChatResponse metadata
        if (params != null && params.containsKey(RETRIEVED_DOCUMENTS)) {
            @SuppressWarnings("unchecked")
            List<Document> retrievedDocs = (List<Document>) params.get(RETRIEVED_DOCUMENTS);
            
            // 添加到 response metadata
            Map<String, Object> metadata = response.getMetadata();
            if (metadata == null) {
                metadata = new HashMap<>();
            }
            metadata.put(RETRIEVED_DOCUMENTS, retrievedDocs);
        }

        return response;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * 从 Prompt 中提取用户问题
     */
    private String extractUserQuestion(Prompt prompt) {
        List<Message> messages = prompt.getMessages();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        // 查找最后一个 UserMessage
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if (message instanceof UserMessage) {
                return message.getContent();
            }
        }

        return null;
    }

    /**
     * 构建上下文文本
     */
    private String buildContext(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "No relevant context found.";
        }

        return documents.stream()
            .map(doc -> doc.getContent())
            .collect(Collectors.joining("\n\n"));
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private VectorStore vectorStore;
        private String userTextAdvise = DEFAULT_USER_TEXT_ADVISE;
        private int topK = DEFAULT_TOP_K;
        private double similarityThreshold = DEFAULT_SIMILARITY_THRESHOLD;
        private String name = DEFAULT_NAME;
        private int order = DEFAULT_ORDER;

        /**
         * 设置 VectorStore（必需）
         */
        public Builder vectorStore(VectorStore vectorStore) {
            this.vectorStore = vectorStore;
            return this;
        }

        /**
         * 设置自定义的 System Message 模板
         * 模板中使用 {question_answer_context} 作为占位符
         */
        public Builder userTextAdvise(String userTextAdvise) {
            this.userTextAdvise = userTextAdvise;
            return this;
        }

        /**
         * 设置检索的文档数量（Top-K）
         */
        public Builder topK(int topK) {
            this.topK = topK;
            return this;
        }

        /**
         * 设置相似度阈值
         */
        public Builder similarityThreshold(double similarityThreshold) {
            this.similarityThreshold = similarityThreshold;
            return this;
        }

        /**
         * 设置 Advisor 名称
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置执行顺序
         */
        public Builder order(int order) {
            this.order = order;
            return this;
        }

        /**
         * 构建 QuestionAnswerAdvisor
         */
        public QuestionAnswerAdvisor build() {
            if (vectorStore == null) {
                throw new IllegalArgumentException("VectorStore must not be null");
            }
            return new QuestionAnswerAdvisor(this);
        }
    }
}
