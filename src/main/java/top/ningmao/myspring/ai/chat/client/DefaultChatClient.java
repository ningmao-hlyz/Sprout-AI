package top.ningmao.myspring.ai.chat.client;

import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
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

    private DefaultChatClient(ChatModel chatModel,
                              ChatOptions defaultChatOptions,
                              String defaultSystemText,
                              String defaultUserText,
                              TemplateRenderer templateRenderer) {
        this.chatModel = chatModel;
        this.defaultChatOptions = defaultChatOptions;
        this.defaultSystemText = defaultSystemText;
        this.defaultUserText = defaultUserText;
        this.templateRenderer = templateRenderer != null ? templateRenderer : new DefaultTemplateRenderer();
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
                    this.templateRenderer
            );
        }
    }

    /**
     * 默认 Prompt Request 实现
     */
    private class DefaultChatClientPromptRequestSpec implements ChatClientPromptRequestSpec {

        private final List<Message> messages = new ArrayList<>();
        private String userText;
        private Map<String, Object> userParams = new HashMap<>();
        private String systemText;
        private Map<String, Object> systemParams = new HashMap<>();
        private ChatOptions chatOptions;
        private Prompt existingPrompt;

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
        public CallResponseSpec call() {
            // 构建 Prompt
            Prompt prompt;
            if (existingPrompt != null) {
                prompt = existingPrompt;
            } else {
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

                prompt = new Prompt(promptMessages, chatOptions);
            }

            // 调用 ChatModel
            ChatResponse response = chatModel.call(prompt);

            return new DefaultCallResponseSpec(response);
        }

        /**
         * 默认 UserSpec 实现
         */
        private static class DefaultUserSpec implements UserSpec {
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
        private static class DefaultSystemSpec implements SystemSpec {
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
}
