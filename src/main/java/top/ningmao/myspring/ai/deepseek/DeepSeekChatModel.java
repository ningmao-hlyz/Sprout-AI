package top.ningmao.myspring.ai.deepseek;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.ToolMessage;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.model.Generation;
import top.ningmao.myspring.ai.chat.model.StreamingChatModel;
import top.ningmao.myspring.ai.chat.prompt.ChatOptions;
import top.ningmao.myspring.ai.chat.prompt.DeepSeekChatOptions;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.ai.model.ToolCall;
import top.ningmao.myspring.ai.model.function.ToolCallback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        ChatOptions options = prompt.getOptions() != null ? prompt.getOptions() : defaultOptions;
        
        // 如果没有工具，直接调用
        if (options.getTools() == null || options.getTools().isEmpty()) {
            return callWithoutTools(prompt);
        }
        
        // 有工具时，处理工具调用循环
        return callWithTools(prompt, options);
    }

    /**
     * 不带工具的普通调用
     */
    private ChatResponse callWithoutTools(Prompt prompt) {
        try {
            // 1. 构建请求体
            JSONObject requestBody = buildRequestBody(prompt);

            // 2. 发送 HTTP 请求
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(60000)  // 30秒超时
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
     * 带工具的调用（支持 Function Calling）
     */
    private ChatResponse callWithTools(Prompt prompt, ChatOptions options) {
        try {
            // 复制消息列表，用于累积对话历史
            List<Message> messages = new ArrayList<>(prompt.getMessages());
            
            // 最多尝试 5 次工具调用（防止无限循环）
            int maxIterations = 5;
            for (int i = 0; i < maxIterations; i++) {
                // 1. 构建新的 Prompt
                Prompt currentPrompt = new Prompt(messages, prompt.getOptions());
                
                // 2. 调用 API
                JSONObject requestBody = buildRequestBody(currentPrompt);
                HttpResponse response = HttpRequest.post(API_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .body(requestBody.toString())
                        .timeout(30000)
                        .execute();

                if (!response.isOk()) {
                    throw new RuntimeException("DeepSeek API call failed: " + response.getStatus());
                }

                // 3. 解析响应
                JSONObject jsonResponse = JSONUtil.parseObj(response.body());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                
                // 4. 检查是否有工具调用请求
                if (message.containsKey("tool_calls") && message.getJSONArray("tool_calls") != null) {
                    // AI 请求调用工具
                    JSONArray toolCallsJson = message.getJSONArray("tool_calls");
                    
                    // 将 JSON 转换为 ToolCall 对象列表
                    List<ToolCall> toolCalls = new ArrayList<>();
                    for (int j = 0; j < toolCallsJson.size(); j++) {
                        JSONObject toolCallJson = toolCallsJson.getJSONObject(j);
                        JSONObject function = toolCallJson.getJSONObject("function");
                        
                        ToolCall toolCall = new ToolCall(
                            toolCallJson.getStr("id"),
                            toolCallJson.getStr("type"),
                            function.getStr("name"),
                            function.getStr("arguments")
                        );
                        toolCalls.add(toolCall);
                    }
                    
                    // 将 AI 的消息添加到历史（包含工具调用请求）
                    String assistantContent = message.getStr("content");
                    if (assistantContent == null || assistantContent.isEmpty()) {
                        assistantContent = "";  // 工具调用时 content 可能为空
                    }
                    messages.add(new AssistantMessage(assistantContent, toolCalls));
                    
                    // 执行所有工具调用并添加结果
                    for (ToolCall toolCall : toolCalls) {
                        String toolResult = executeToolCall(toolCall, options.getTools());
                        messages.add(new ToolMessage(toolCall.id(), toolResult));
                    }
                    
                    // 继续循环，让 AI 使用工具结果生成最终响应
                    continue;
                }
                
                // 5. 没有工具调用，返回最终响应
                String content = message.getStr("content");
                AssistantMessage assistantMessage = new AssistantMessage(content);
                Generation generation = new Generation(assistantMessage);
                List<Generation> generations = new ArrayList<>();
                generations.add(generation);
                return new ChatResponse(generations);
            }
            
            throw new RuntimeException("Tool calling exceeded maximum iterations");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to call DeepSeek API with tools", e);
        }
    }

    /**
     * 查找工具
     */
    private ToolCallback findTool(List<ToolCallback> tools, String toolName) {
        for (ToolCallback tool : tools) {
            if (tool.getName().equals(toolName)) {
                return tool;
            }
        }
        return null;
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
            
            // 处理 ToolMessage 的特殊字段
            if (message instanceof ToolMessage) {
                ToolMessage toolMsg = (ToolMessage) message;
                msg.set("tool_call_id", toolMsg.getToolCallId());
            }
            
            // 处理 AssistantMessage 的 tool_calls
            if (message instanceof AssistantMessage) {
                AssistantMessage assistantMsg = (AssistantMessage) message;
                if (assistantMsg.getToolCalls() != null && !assistantMsg.getToolCalls().isEmpty()) {
                    JSONArray toolCallsArray = new JSONArray();
                    for (ToolCall toolCall : assistantMsg.getToolCalls()) {
                        JSONObject toolCallObj = new JSONObject();
                        toolCallObj.set("id", toolCall.id());
                        toolCallObj.set("type", toolCall.type());
                        
                        JSONObject function = new JSONObject();
                        function.set("name", toolCall.functionName());
                        function.set("arguments", toolCall.arguments());
                        
                        toolCallObj.set("function", function);
                        toolCallsArray.add(toolCallObj);
                    }
                    msg.set("tool_calls", toolCallsArray);
                }
            }
            
            messages.add(msg);
        }
        requestBody.set("messages", messages);

        // 添加工具定义（Function Calling 支持）
        if (options.getTools() != null && !options.getTools().isEmpty()) {
            JSONArray tools = new JSONArray();
            for (ToolCallback tool : options.getTools()) {
                JSONObject toolDef = new JSONObject();
                toolDef.set("type", "function");
                
                JSONObject function = new JSONObject();
                function.set("name", tool.getToolDefinition().name());
                function.set("description", tool.getToolDefinition().description());
                function.set("parameters", JSONUtil.parseObj(tool.getToolDefinition().inputSchema()));
                
                toolDef.set("function", function);
                tools.add(toolDef);
            }
            requestBody.set("tools", tools);
        }

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
     * 流式调用 AI 模型（支持 SSE 和 Function Calling）
     *
     * @param prompt        提示词对象
     * @param chunkConsumer 内容片段消费者
     */
    @Override
    public void stream(Prompt prompt, Consumer<String> chunkConsumer) {
        ChatOptions options = prompt.getOptions() != null ? prompt.getOptions() : defaultOptions;
        
        // 如果没有工具，直接流式调用
        if (options.getTools() == null || options.getTools().isEmpty()) {
            streamWithoutTools(prompt, chunkConsumer);
        } else {
            // 有工具时，处理工具调用循环（流式）
            streamWithTools(prompt, options, chunkConsumer);
        }
    }

    /**
     * 不带工具的流式调用
     */
    private void streamWithoutTools(Prompt prompt, Consumer<String> chunkConsumer) {
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

    /**
     * 带工具的流式调用（支持 Function Calling）
     */
    private void streamWithTools(Prompt prompt, ChatOptions options, Consumer<String> chunkConsumer) {
        try {
            // 复制消息列表，用于累积对话历史
            List<Message> messages = new ArrayList<>(prompt.getMessages());
            
            // 最多尝试 5 次工具调用（防止无限循环）
            int maxIterations = 5;
            for (int i = 0; i < maxIterations; i++) {
                // 1. 构建新的 Prompt
                Prompt currentPrompt = new Prompt(messages, prompt.getOptions());
                
                // 2. 收集流式响应和工具调用
                StringBuilder assistantMessage = new StringBuilder();
                List<ToolCall> toolCalls = new ArrayList<>();
                Map<Integer, ToolCallBuilder> toolCallBuilders = new HashMap<>();
                
                // 3. 进行流式调用并收集数据
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                
                try {
                    JSONObject requestBody = buildRequestBody(currentPrompt);
                    requestBody.set("stream", true);
                    byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);

                    URL url = new URL(API_URL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "text/event-stream");
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(30000);
                    connection.setReadTimeout(60000);

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(requestBodyBytes);
                        os.flush();
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        throw new RuntimeException("DeepSeek API call failed: " + responseCode);
                    }

                    reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), StandardCharsets.UTF_8));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() || line.startsWith(":")) {
                            continue;
                        }

                        if (line.startsWith("data: ")) {
                            String data = line.substring(6).trim();

                            if ("[DONE]".equals(data)) {
                                break;
                            }

                            try {
                                JSONObject json = JSONUtil.parseObj(data);
                                JSONArray choices = json.getJSONArray("choices");

                                if (choices != null && !choices.isEmpty()) {
                                    JSONObject choice = choices.getJSONObject(0);
                                    JSONObject delta = choice.getJSONObject("delta");

                                    if (delta != null) {
                                        // 处理 content
                                        if (delta.containsKey("content")) {
                                            String content = delta.getStr("content");
                                            if (content != null && !content.isEmpty()) {
                                                assistantMessage.append(content);
                                                chunkConsumer.accept(content);
                                            }
                                        }

                                        // 处理 tool_calls（流式）
                                        if (delta.containsKey("tool_calls")) {
                                            JSONArray toolCallsArray = delta.getJSONArray("tool_calls");
                                            for (int j = 0; j < toolCallsArray.size(); j++) {
                                                JSONObject toolCallDelta = toolCallsArray.getJSONObject(j);
                                                int index = toolCallDelta.getInt("index");
                                                
                                                ToolCallBuilder builder = toolCallBuilders.computeIfAbsent(
                                                        index, k -> new ToolCallBuilder());

                                                if (toolCallDelta.containsKey("id")) {
                                                    builder.id = toolCallDelta.getStr("id");
                                                }
                                                if (toolCallDelta.containsKey("type")) {
                                                    builder.type = toolCallDelta.getStr("type");
                                                }
                                                if (toolCallDelta.containsKey("function")) {
                                                    JSONObject function = toolCallDelta.getJSONObject("function");
                                                    if (function.containsKey("name")) {
                                                        builder.functionName = function.getStr("name");
                                                    }
                                                    if (function.containsKey("arguments")) {
                                                        builder.arguments.append(function.getStr("arguments"));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Failed to parse stream chunk: " + data);
                            }
                        }
                    }
                } finally {
                    if (reader != null) reader.close();
                    if (connection != null) connection.disconnect();
                }

                // 4. 如果没有工具调用，结束循环
                if (toolCallBuilders.isEmpty()) {
                    break;
                }

                // 5. 构建工具调用列表
                for (ToolCallBuilder builder : toolCallBuilders.values()) {
                    toolCalls.add(new ToolCall(
                            builder.id,
                            builder.type,
                            builder.functionName,
                            builder.arguments.toString()
                    ));
                }
                
                // 6. 将 AI 响应（包含工具调用）添加到消息历史
                messages.add(new AssistantMessage(assistantMessage.toString(), toolCalls));
                
                // 7. 执行工具调用
                for (ToolCall toolCall : toolCalls) {
                    String toolResult = executeToolCall(toolCall, options.getTools());
                    messages.add(new ToolMessage(toolCall.id(), toolResult));
                }
                
                // 8. 继续下一轮调用（AI 会基于工具结果生成最终回复）
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to call DeepSeek streaming API with tools", e);
        }
    }

    /**
     * 执行单个工具调用
     *
     * @param toolCall 工具调用
     * @param tools    可用工具列表
     * @return 工具执行结果
     */
    private String executeToolCall(ToolCall toolCall, List<ToolCallback> tools) {
        // 找到对应的工具
        ToolCallback tool = findTool(tools, toolCall.functionName());
        if (tool == null) {
            throw new RuntimeException("Tool not found: " + toolCall.functionName());
        }
        
        // 执行工具并返回结果
        return tool.call(toolCall.arguments());
    }

    /**
     * 工具调用构建器（用于累积流式 tool_calls）
     */
    private static class ToolCallBuilder {
        String id;
        String type = "function";
        String functionName;
        StringBuilder arguments = new StringBuilder();
    }
}
