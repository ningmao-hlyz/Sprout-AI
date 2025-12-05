package top.ningmao.myspring.ai.demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import top.ningmao.myspring.ai.chat.advisor.QuestionAnswerAdvisor;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.document.Document;
import top.ningmao.myspring.bean.factory.annotation.Autowired;
import top.ningmao.myspring.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat HTTP Controller
 * 提供简单的 REST API
 *
 * @author 宁猫
 * @since 2025-12-04
 */
@Component
public class ChatController implements HttpHandler {

    @Autowired
    private AIConfiguration aiConfiguration;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 设置CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // 处理 OPTIONS 预检请求
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("/api/chat".equals(path) && "POST".equals(method)) {
                handleChatRequest(exchange);
            } else if ("/api/chat/stream".equals(path) && "POST".equals(method)) {
                handleStreamChatRequest(exchange);
            } else if ("/api/health".equals(path) && "GET".equals(method)) {
                handleHealthCheck(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}";
            sendResponse(exchange, 500, error);
        }
    }

    /**
     * 处理聊天请求（非流式）
     */
    private void handleChatRequest(HttpExchange exchange) throws IOException {
        // 读取请求体
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // 简单解析 JSON（手动实现，避免依赖）
        Map<String, String> request = parseJson(body);
        String message = request.get("message");

        if (message == null || message.isBlank()) {
            sendResponse(exchange, 400, "{\"error\":\"Message is required\"}");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("[用户] " + message);
        System.out.println("=".repeat(80));

        // 1. 调用 ChatClient（自动 RAG 检索）
        ChatResponse response = aiConfiguration.getChatClient().prompt()
                .user(message)
                .call()
                .chatResponse();

        // 2. 从 metadata 获取检索到的文档并打印
        printRetrievedDocuments(response);

        // 3. 打印答案
        String answer = response.getResult().getOutput().getContent();
        System.out.println("\n[助手回答]");
        System.out.println("-".repeat(80));
        System.out.println(answer);
        System.out.println("-".repeat(80) + "\n");
        
        // 返回响应
        String jsonResponse = "{\"answer\":\"" + escapeJson(answer) + "\"}";
        sendResponse(exchange, 200, jsonResponse);
    }

    /**
     * 处理流式聊天请求
     */
    private void handleStreamChatRequest(HttpExchange exchange) throws IOException {
        // 读取请求体
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        Map<String, String> request = parseJson(body);
        String message = request.get("message");

        if (message == null || message.isBlank()) {
            sendResponse(exchange, 400, "{\"error\":\"Message is required\"}");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("[用户] " + message);
        System.out.println("=".repeat(80));

        // 设置 SSE 响应头
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(200, 0);

        OutputStream os = exchange.getResponseBody();

        try {
            System.out.println("\n[助手回答（流式）]");
            System.out.println("-".repeat(80));
            
            // 流式调用
            aiConfiguration.getChatClient().prompt()
                    .user(message)
                    .stream()
                    .content(chunk -> {
                        try {
                            // 输出内容（DeepSeek-R1 会在 content 中包含思考过程）
                            if (chunk != null && !chunk.isEmpty()) {
                                String event = "data: {\"type\":\"content\",\"content\":\"" +
                                        escapeJson(chunk) + "\"}\n\n";
                                os.write(event.getBytes(StandardCharsets.UTF_8));
                                os.flush();
                                System.out.print(chunk);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // 发送结束标记
            os.write("data: {\"type\":\"done\"}\n\n".getBytes(StandardCharsets.UTF_8));
            os.flush();
            System.out.println("\n" + "=".repeat(80));

        } finally {
            os.close();
        }
    }

    /**
     * 从 ChatResponse metadata 中打印检索到的文档（避免重复检索）
     */
    private void printRetrievedDocuments(ChatResponse response) {
        System.out.println("\n[RAG 检索上下文]");
        System.out.println("-".repeat(80));

        // 从 metadata 获取 QuestionAnswerAdvisor 检索的文档
        Map<String, Object> metadata = response.getMetadata();
        if (metadata == null || !metadata.containsKey(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS)) {
            System.out.println("  未找到检索文档（Advisor 可能未启用）");
            System.out.println("-".repeat(80));
            return;
        }

        @SuppressWarnings("unchecked")
        List<Document> documents = (List<Document>) metadata.get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);

        if (documents.isEmpty()) {
            System.out.println("  未检索到相关文档");
        } else {
            System.out.println(" 检索到 " + documents.size() + " 个相关文档块：\n");
            for (int i = 0; i < documents.size(); i++) {
                Document doc = documents.get(i);
                System.out.println("【文档 " + (i + 1) + "】相似度: " +
                        String.format("%.3f", doc.getScore()));
                
                // 显示完整内容（如果太长则截断到500字符）
                String content = doc.getContent();
                int maxLength = 500;
                if (content.length() > maxLength) {
                    System.out.println("内容: " + content.substring(0, maxLength) + "...\n");
                } else {
                    System.out.println("内容: " + content + "\n");
                }
                System.out.println("-".repeat(40));
            }
        }
        System.out.println("-".repeat(80));
    }

    /**
     * 健康检查
     */
    private void handleHealthCheck(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, "{\"status\":\"ok\"}");
    }

    /**
     * 发送 HTTP 响应
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    /**
     * 简单的 JSON 解析（仅支持简单对象）
     */
    private Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        
        // 移除 { }
        json = json.trim().replaceAll("^\\{|\\}$", "");
        
        // 分割键值对
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("\"", "");
                String value = kv[1].trim().replaceAll("\"", "");
                result.put(key, value);
            }
        }
        
        return result;
    }

    /**
     * JSON 字符串转义
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
