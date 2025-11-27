package top.ningmao.myspring.ai.chat.client;

import top.ningmao.myspring.ai.chat.prompt.ChatOptions;

import java.util.Map;
import java.util.function.Consumer;


/**
 * ChatClient Prompt 请求规范
 * 定义如何构建提示词的流式 API
 *
 * @author 宁猫
 * @since 2025-11-27 14:53:20
 */
public interface ChatClientPromptRequestSpec {

    /**
     * 设置用户消息
     *
     * @param text 用户消息文本
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec user(String text);

    /**
     * 设置用户消息（带Consumer配置）
     *
     * @param userSpec 用户消息配置
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec user(Consumer<UserSpec> userSpec);

    /**
     * 设置系统消息
     *
     * @param text 系统消息文本
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec system(String text);

    /**
     * 设置系统消息（带Consumer配置）
     *
     * @param systemSpec 系统消息配置
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec system(Consumer<SystemSpec> systemSpec);

    /**
     * 设置 ChatOptions
     *
     * @param chatOptions ChatOptions
     * @return ChatClientPromptRequestSpec
     */
    ChatClientPromptRequestSpec options(ChatOptions chatOptions);

    /**
     * 调用 AI 模型
     *
     * @return CallResponseSpec
     */
    CallResponseSpec call();

    /**
     * 用户消息配置规范
     */
    interface UserSpec {

        /**
         * 设置用户消息文本
         *
         * @param text 文本（可能包含占位符）
         * @return UserSpec
         */
        UserSpec text(String text);

        /**
         * 设置模板参数
         *
         * @param key   参数名
         * @param value 参数值
         * @return UserSpec
         */
        UserSpec param(String key, Object value);

        /**
         * 设置多个模板参数
         *
         * @param params 参数映射
         * @return UserSpec
         */
        UserSpec params(Map<String, Object> params);
    }

    /**
     * 系统消息配置规范
     */
    interface SystemSpec {

        /**
         * 设置系统消息文本
         *
         * @param text 文本（可能包含占位符）
         * @return SystemSpec
         */
        SystemSpec text(String text);

        /**
         * 设置模板参数
         *
         * @param key   参数名
         * @param value 参数值
         * @return SystemSpec
         */
        SystemSpec param(String key, Object value);

        /**
         * 设置多个模板参数
         *
         * @param params 参数映射
         * @return SystemSpec
         */
        SystemSpec params(Map<String, Object> params);
    }
}
