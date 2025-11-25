package top.ningmao.myspring.ai.deepseek;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.model.Generation;
import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;



/**
 * DeepSeek ChatModel 实现
 * 使用 DeepSeek API 进行 AI 对话
 * <p>
 * API 文档：https://platform.deepseek.com/api-docs/
 *
 * @author 宁猫
 * @since 2025-11-25 20:54:10
 */
public class DeepSeekChatModel implements ChatModel {

    /**
     * DeepSeek API 地址
     */
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    /**
     * API 密钥
     */
    private final String apiKey;

    /**
     * 默认配置选项
     */
    private final DeepSeekChatOptions defaultOptions;

    public DeepSeekChatModel(String apiKey) {
        this(apiKey, new DeepSeekChatOptions());
    }

    public DeepSeekChatModel(String apiKey, DeepSeekChatOptions defaultOptions) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("DeepSeek API key cannot be null or empty");
        }
        this.apiKey = apiKey;
        this.defaultOptions = defaultOptions;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        try {
            // 1. 构建请求体
            JSONObject requestBody = buildRequestBody(prompt);

            // 2. 发送 HTTP 请求
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(30000)  // 30秒超时
                    .execute();

            // 3. 检查响应状态
            if (!response.isOk()) {
                throw new RuntimeException("DeepSeek API call failed: " + response.getStatus() +
                        ", body: " + response.body());
            }

            // 4. 解析响应
            return parseResponse(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Failed to call DeepSeek API", e);
        }
    }

    /**
     * 构建 API 请求体
     */
    private JSONObject buildRequestBody(Prompt prompt) {
        JSONObject requestBody = new JSONObject();

        // 获取配置选项（优先使用 Prompt 中的配置）
        ChatOptions options = prompt.getOptions() != null ? prompt.getOptions() : defaultOptions;

        // 设置模型和参数
        requestBody.set("model", options.getModel());
        
        if (options.getTemperature() != null) {
            requestBody.set("temperature", options.getTemperature());
        }
        if (options.getMaxTokens() != null) {
            requestBody.set("max_tokens", options.getMaxTokens());
        }
        if (options.getTopP() != null) {
            requestBody.set("top_p", options.getTopP());
        }
        if (options.getFrequencyPenalty() != null) {
            requestBody.set("frequency_penalty", options.getFrequencyPenalty());
        }
        if (options.getPresencePenalty() != null) {
            requestBody.set("presence_penalty", options.getPresencePenalty());
        }

        // 构建消息数组
        JSONArray messages = new JSONArray();
        for (Message message : prompt.getMessages()) {
            JSONObject msg = new JSONObject();
            msg.set("role", message.getMessageType().getValue());
            msg.set("content", message.getContent());
            messages.add(msg);
        }
        requestBody.set("messages", messages);

        return requestBody;
    }

    /**
     * 解析 API 响应
     */
    private ChatResponse parseResponse(String responseBody) {
        try {
            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);

            // 检查是否有错误
            if (jsonResponse.containsKey("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                throw new RuntimeException("DeepSeek API error: " + error.getStr("message"));
            }

            // 解析 choices
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices in DeepSeek API response");
            }

            // 解析第一个 choice
            JSONObject choice = choices.getJSONObject(0);
            JSONObject messageJson = choice.getJSONObject("message");
            String content = messageJson.getStr("content");

            // 创建 Generation
            AssistantMessage assistantMessage = new AssistantMessage(content);
            Generation generation = new Generation(assistantMessage);

            // 创建 ChatResponse
            List<Generation> generations = new ArrayList<>();
            generations.add(generation);

            return new ChatResponse(generations);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DeepSeek API response", e);
        }
    }

    /**
     * 获取 API 密钥
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 获取默认配置
     */
    public DeepSeekChatOptions getDefaultOptions() {
        return defaultOptions;
    }

    @Override
    public String toString() {
        return "DeepSeekChatModel{" +
                "apiKey='" + maskApiKey(apiKey) + '\'' +
                ", defaultOptions=" + defaultOptions +
                '}';
    }

    /**
     * 掩码 API Key，用于日志输出
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        return apiKey.substring(0, 3) + "..." + apiKey.substring(apiKey.length() - 3);
    }
}
