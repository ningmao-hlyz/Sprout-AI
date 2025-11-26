package top.ningmao.myspring.ai.deepseek;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.model.Generation;
import top.ningmao.myspring.ai.chat.model.StreamingChatModel;
import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;



/**
 * DeepSeek ChatModel 实现
 * 使用 DeepSeek API 进行 AI 对话，支持普通调用和流式响应
 * <p>
 * API 文档：https://platform.deepseek.com/api-docs/
 *
 * @author 宁猫
 * @since 2025-11-25 20:54:10
 */
public class DeepSeekChatModel implements StreamingChatModel {

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

    /**
     * 流式调用 DeepSeek API
     * <p>
     * 使用 Server-Sent Events (SSE) 格式接收流式响应
     * 每次收到新的内容片段时，会调用 chunkConsumer
     *
     * @param prompt        提示词对象
     * @param chunkConsumer 内容片段消费者
     */
    @Override
    public void stream(Prompt prompt, Consumer<String> chunkConsumer) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        try {
            // 1. 构建请求体（开启流式模式）
            JSONObject requestBody = buildRequestBody(prompt);
            requestBody.set("stream", true);  // 开启流式响应
            byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);

            // 2. 创建 HTTP 连接
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "text/event-stream");  // SSE 格式
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);

            // 3. 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBodyBytes);
                os.flush();
            }

            // 4. 检查响应状态
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("DeepSeek API call failed: " + responseCode);
            }

            // 5. 实时读取流式响应
            reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过空行和注释
                if (line.trim().isEmpty() || line.startsWith(":")) {
                    continue;
                }

                // 解析 data: 开头的行
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();

                    // 结束标记
                    if ("[DONE]".equals(data)) {
                        break;
                    }

                    // 解析 JSON 并提取 content
                    try {
                        JSONObject json = JSONUtil.parseObj(data);
                        JSONArray choices = json.getJSONArray("choices");

                        if (choices != null && !choices.isEmpty()) {
                            JSONObject choice = choices.getJSONObject(0);
                            JSONObject delta = choice.getJSONObject("delta");

                            if (delta != null && delta.containsKey("content")) {
                                String content = delta.getStr("content");
                                if (content != null && !content.isEmpty()) {
                                    // 实时回调消费者
                                    chunkConsumer.accept(content);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 忽略单个片段解析错误，继续处理下一个
                        System.err.println("Failed to parse stream chunk: " + data);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to call DeepSeek streaming API", e);
        } finally {
            // 关闭资源
            try {
                if (reader != null) {
                    reader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                // 忽略关闭错误
            }
        }
    }

}
