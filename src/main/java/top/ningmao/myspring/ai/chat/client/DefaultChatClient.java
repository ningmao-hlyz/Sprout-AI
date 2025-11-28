package top.ningmao.myspring.ai.chat.client;

import top.ningmao.myspring.ai.chat.advisor.Advisor;
import top.ningmao.myspring.ai.chat.advisor.AdvisorChainExecutor;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.model.StreamingChatModel;
import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.ai.chat.prompt.template.DefaultTemplateRenderer;
import top.ningmao.myspring.ai.chat.prompt.template.TemplateRenderer;

import java.util.*;
import java.util.function.Consumer;


/**
 * ChatClient 默认实现
 * 参考 Spring AI 的 DefaultChatClient 实现
 *
 * @author 宁猫
 * @since 2025-11-27 14:54:19
 */
public class DefaultChatClient implements ChatClient {

    private final ChatModel chatModel;
    private final ChatOptions defaultChatOptions;
    private final String defaultSystemText;
    private final String defaultUserText;
    private final TemplateRenderer templateRenderer;
    private final List<Advisor> defaultAdvisors;

    private DefaultChatClient(ChatModel chatModel,
                              ChatOptions defaultChatOptions,
                              String defaultSystemText,
                              String defaultUserText,
                              TemplateRenderer templateRenderer,
                              List<Advisor> defaultAdvisors) {
        this.chatModel = chatModel;
        this.defaultChatOptions = defaultChatOptions;
        this.defaultSystemText = defaultSystemText;
        this.defaultUserText = defaultUserText;
        this.templateRenderer = templateRenderer != null ? templateRenderer : new DefaultTemplateRenderer();
        this.defaultAdvisors = defaultAdvisors != null ? defaultAdvisors : Collections.emptyList();
    }

    @Override
    public ChatClientPromptRequestSpec prompt() {
        return new DefaultChatClientPromptRequestSpec();
    }

    @Override
    public ChatClientPromptRequestSpec prompt(Prompt prompt) {
        return new DefaultChatClientPromptRequestSpec(prompt);
    }

    @Override
    public ChatClientPromptRequestSpec prompt(String content) {
        return new DefaultChatClientPromptRequestSpec().user(content);
    }

    @Override
    public Builder mutate() {
        return new DefaultBuilder(this.chatModel)
                .defaultOptions(this.defaultChatOptions)
                .defaultSystem(this.defaultSystemText)
                .defaultUser(this.defaultUserText);
    }

    /**
     * 默认 Builder 实现
     */
    public static class DefaultBuilder implements Builder {

        private final ChatModel chatModel;
        private ChatOptions defaultChatOptions;
        private String defaultSystemText;
        private String defaultUserText;
        private TemplateRenderer templateRenderer;
        private final List<Advisor> defaultAdvisors = new ArrayList<>();

        public DefaultBuilder(ChatModel chatModel) {
            if (chatModel == null) {
                throw new IllegalArgumentException("ChatModel cannot be null");
            }
            this.chatModel = chatModel;
        }

        @Override
        public Builder defaultOptions(ChatOptions chatOptions) {
            this.defaultChatOptions = chatOptions;
            return this;
        }

        @Override
        public Builder defaultSystem(String text) {
            this.defaultSystemText = text;
            return this;
        }

        @Override
        public Builder defaultUser(String text) {
            this.defaultUserText = text;
            return this;
        }

        @Override
        public Builder defaultAdvisors(Advisor... advisors) {
            if (advisors != null && advisors.length > 0) {
                this.defaultAdvisors.addAll(Arrays.asList(advisors));
            }
            return this;
        }

        public Builder templateRenderer(TemplateRenderer templateRenderer) {
            this.templateRenderer = templateRenderer;
            return this;
        }

        @Override
        public ChatClient build() {
            return new DefaultChatClient(
                    this.chatModel,
                    this.defaultChatOptions,
                    this.defaultSystemText,
                    this.defaultUserText,
                    this.templateRenderer,
                    this.defaultAdvisors
            );
        }
    }

    /**
     * 默认 Prompt Request 实现
     */
    private class DefaultChatClientPromptRequestSpec implements ChatClientPromptRequestSpec {

        private final List<Message> messages = new ArrayList<>();
        private String userText;
        private final Map<String, Object> userParams = new HashMap<>();
        private String systemText;
        private final Map<String, Object> systemParams = new HashMap<>();
        private ChatOptions chatOptions;
        private Prompt existingPrompt;
        
        // Advisor 相关
        private final Map<String, Object> advisorParams = new HashMap<>();
        private List<Advisor> runtimeAdvisors = null; // null 表示使用 defaultAdvisors

        public DefaultChatClientPromptRequestSpec() {
            // 应用默认值
            if (defaultSystemText != null) {
                this.systemText = defaultSystemText;
            }
            if (defaultUserText != null) {
                this.userText = defaultUserText;
            }
            if (defaultChatOptions != null) {
                this.chatOptions = defaultChatOptions;
            }
        }

        public DefaultChatClientPromptRequestSpec(Prompt prompt) {
            this.existingPrompt = prompt;
        }

        @Override
        public ChatClientPromptRequestSpec user(String text) {
            this.userText = text;
            return this;
        }

        @Override
        public ChatClientPromptRequestSpec user(Consumer<UserSpec> userSpec) {
            DefaultUserSpec spec = new DefaultUserSpec();
            userSpec.accept(spec);
            this.userText = spec.text;
            this.userParams.putAll(spec.params);
            return this;
        }

        @Override
        public ChatClientPromptRequestSpec system(String text) {
            this.systemText = text;
            return this;
        }

        @Override
        public ChatClientPromptRequestSpec system(Consumer<SystemSpec> systemSpec) {
            DefaultSystemSpec spec = new DefaultSystemSpec();
            systemSpec.accept(spec);
            this.systemText = spec.text;
            this.systemParams.putAll(spec.params);
            return this;
        }

        @Override
        public ChatClientPromptRequestSpec options(ChatOptions chatOptions) {
            this.chatOptions = chatOptions;
            return this;
        }

        @Override
        public ChatClientPromptRequestSpec advisors(Consumer<AdvisorSpec> advisorSpecConsumer) {
            DefaultAdvisorSpec spec = new DefaultAdvisorSpec();
            advisorSpecConsumer.accept(spec);
            this.advisorParams.putAll(spec.params);
            if (spec.advisors != null) {
                this.runtimeAdvisors = spec.advisors;
            }
            return this;
        }

        @Override
        public CallResponseSpec call() {
            // 1. 构建原始 Prompt
            Prompt prompt = buildPrompt();

            // 2. 确定使用哪些 Advisors（运行时 > 默认）
            List<Advisor> advisorsToUse = runtimeAdvisors != null ? runtimeAdvisors : defaultAdvisors;

            // 3. 应用 Advisor 链（请求前）
            if (!advisorsToUse.isEmpty()) {
                AdvisorChainExecutor executor = new AdvisorChainExecutor(advisorsToUse);
                prompt = executor.adviseRequest(prompt, advisorParams);
            }

            // 4. 调用 ChatModel
            ChatResponse response = chatModel.call(prompt);
            String content = response.getOutput();

            // 5. 应用 Advisor 链（响应后）
            if (!advisorsToUse.isEmpty()) {
                AdvisorChainExecutor executor = new AdvisorChainExecutor(advisorsToUse);
                content = executor.adviseResponse(prompt, content, advisorParams);
                // 更新 response 的内容
                response = new ChatResponse(content);
            }

            return new DefaultCallResponseSpec(response);
        }

        /**
         * 构建 Prompt
         */
        private Prompt buildPrompt() {
            if (existingPrompt != null) {
                return existingPrompt;
            }

            List<Message> promptMessages = new ArrayList<>();

            // 添加系统消息
            if (systemText != null && !systemText.isBlank()) {
                String renderedSystemText = templateRenderer.apply(systemText, systemParams);
                promptMessages.add(new SystemMessage(renderedSystemText));
            }

            // 添加用户消息
            if (userText != null && !userText.isBlank()) {
                String renderedUserText = templateRenderer.apply(userText, userParams);
                promptMessages.add(new UserMessage(renderedUserText));
            }

            // 添加其他消息
            promptMessages.addAll(messages);

            return new Prompt(promptMessages, chatOptions);
        }

        @Override
        public StreamResponseSpec stream() {
            // 1. 构建原始 Prompt
            Prompt prompt = buildPrompt();

            // 2. 确定使用哪些 Advisors（运行时 > 默认）
            List<Advisor> advisorsToUse = runtimeAdvisors != null ? runtimeAdvisors : defaultAdvisors;

            // 3. 应用 Advisor 链（请求前）
            if (!advisorsToUse.isEmpty()) {
                AdvisorChainExecutor executor = new AdvisorChainExecutor(advisorsToUse);
                prompt = executor.adviseRequest(prompt, advisorParams);
            }

            // 4. 检查 ChatModel 是否支持流式
            if (!(chatModel instanceof StreamingChatModel)) {
                throw new UnsupportedOperationException(
                        "The ChatModel does not support streaming. Please use a StreamingChatModel implementation.");
            }

            // 5. 返回流式响应
            return new DefaultStreamResponseSpec((StreamingChatModel) chatModel, prompt, advisorsToUse, advisorParams);
        }

        /**
         * 默认 UserSpec 实现
         */
        private class DefaultUserSpec implements UserSpec {
            private String text;
            private final Map<String, Object> params = new HashMap<>();

            @Override
            public UserSpec text(String text) {
                this.text = text;
                return this;
            }

            @Override
            public UserSpec param(String key, Object value) {
                this.params.put(key, value);
                return this;
            }

            @Override
            public UserSpec params(Map<String, Object> params) {
                this.params.putAll(params);
                return this;
            }
        }

        /**
         * 默认 SystemSpec 实现
         */
        private class DefaultSystemSpec implements SystemSpec {
            private String text;
            private final Map<String, Object> params = new HashMap<>();

            @Override
            public SystemSpec text(String text) {
                this.text = text;
                return this;
            }

            @Override
            public SystemSpec param(String key, Object value) {
                this.params.put(key, value);
                return this;
            }

            @Override
            public SystemSpec params(Map<String, Object> params) {
                this.params.putAll(params);
                return this;
            }
        }

        /**
         * 默认 AdvisorSpec 实现
         */
        private class DefaultAdvisorSpec implements AdvisorSpec {
            private final Map<String, Object> params = new HashMap<>();
            private List<Advisor> advisors = null;

            @Override
            public AdvisorSpec param(String key, Object value) {
                this.params.put(key, value);
                return this;
            }

            @Override
            public AdvisorSpec params(Map<String, Object> params) {
                this.params.putAll(params);
                return this;
            }

            @Override
            public AdvisorSpec advisors(Advisor... advisors) {
                if (advisors != null && advisors.length > 0) {
                    this.advisors = Arrays.asList(advisors);
                }
                return this;
            }

            @Override
            public AdvisorSpec advisors(List<Advisor> advisors) {
                this.advisors = advisors;
                return this;
            }
        }
    }

    /**
     * 默认 CallResponseSpec 实现
     */
    private static class DefaultCallResponseSpec implements CallResponseSpec {

        private final ChatResponse chatResponse;

        public DefaultCallResponseSpec(ChatResponse chatResponse) {
            this.chatResponse = chatResponse;
        }

        @Override
        public String content() {
            return chatResponse.getOutput();
        }

        @Override
        public ChatResponse chatResponse() {
            return chatResponse;
        }
    }

    /**
     * 默认 StreamResponseSpec 实现
     */
    private static class DefaultStreamResponseSpec implements StreamResponseSpec {

        private final StreamingChatModel streamingChatModel;
        private final Prompt prompt;
        private final List<Advisor> advisors;
        private final Map<String, Object> advisorParams;

        public DefaultStreamResponseSpec(StreamingChatModel streamingChatModel, Prompt prompt,
                                         List<Advisor> advisors, Map<String, Object> advisorParams) {
            this.streamingChatModel = streamingChatModel;
            this.prompt = prompt;
            this.advisors = advisors;
            this.advisorParams = advisorParams;
        }

        @Override
        public void content(Consumer<String> consumer) {
            // 流式输出：直接调用 StreamingChatModel
            // 注意：响应后的 Advisor 在流式场景下较复杂，需要累积完整响应后处理
            if (advisors != null && !advisors.isEmpty()) {
                // 累积完整响应后应用 Advisor
                StringBuilder fullResponse = new StringBuilder();
                streamingChatModel.stream(prompt, chunk -> {
                    fullResponse.append(chunk);
                    consumer.accept(chunk); // 实时输出
                });
                
                // 流式完成后，应用响应后的 Advisor（保存到历史等）
                if (fullResponse.length() > 0) {
                    AdvisorChainExecutor executor = new AdvisorChainExecutor(advisors);
                    executor.adviseResponse(prompt, fullResponse.toString(), advisorParams);
                }
            } else {
                // 无 Advisor，直接调用
                streamingChatModel.stream(prompt, consumer);
            }
        }

        @Override
        public void chatResponse(Consumer<ChatResponse> consumer) {
            // 调用 StreamingChatModel 的 stream 方法，将每个字符串块包装成 ChatResponse
            streamingChatModel.stream(prompt, chunk -> {
                // 简单包装：每个chunk作为一个 ChatResponse
                if (chunk != null && !chunk.isEmpty()) {
                    ChatResponse response = new ChatResponse(chunk);
                    consumer.accept(response);
                }
            });
        }
    }
}
