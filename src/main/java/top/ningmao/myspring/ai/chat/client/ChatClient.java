package top.ningmao.myspring.ai.chat.client;

import top.ningmao.myspring.ai.chat.advisor.Advisor;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;


/**
 * ChatClient 接口
 * 提供流式 API 来与 AI 模型交互
 *
 * @author 宁猫
 * @since 2025-11-27 14:48:30
 */
public interface ChatClient {

    /**
     * 开始构建提示词（无参数）
     *
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec prompt();

    /**
     * 使用现有 Prompt 开始构建
     *
     * @param prompt Prompt 对象
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec prompt(Prompt prompt);

    /**
     * 使用用户文本开始构建（便捷方法）
     *
     * @param content 用户消息内容
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec prompt(String content);

    /**
     * 创建一个新的 Builder，复制当前 ChatClient 的默认设置
     *
     * @return Builder
     */
    Builder mutate();

    /**
     * 从 ChatModel 创建 ChatClient
     *
     * @param chatModel ChatModel 实例
     * @return ChatClient
     */
    static ChatClient create(ChatModel chatModel) {
        return builder(chatModel).build();
    }

    /**
     * 创建 Builder
     *
     * @param chatModel ChatModel 实例
     * @return Builder
     */
    static Builder builder(ChatModel chatModel) {
        return new DefaultChatClient.DefaultBuilder(chatModel);
    }

    /**
     * ChatClient Builder
     */
    interface Builder {

        /**
         * 设置默认的 ChatOptions
         *
         * @param chatOptions ChatOptions
         * @return Builder
         */
        Builder defaultOptions(ChatOptions chatOptions);

        /**
         * 设置默认的系统消息
         *
         * @param text 系统消息文本
         * @return Builder
         */
        Builder defaultSystem(String text);

        /**
         * 设置默认的用户消息
         *
         * @param text 用户消息文本
         * @return Builder
         */
        Builder defaultUser(String text);

        /**
         * 设置默认的 Advisors
         * 这些 Advisor 会自动应用到所有请求
         *
         * @param advisors Advisor 列表
         * @return Builder
         */
        Builder defaultAdvisors(Advisor... advisors);

        /**
         * 构建 ChatClient
         *
         * @return ChatClient
         */
        ChatClient build();
    }
}
