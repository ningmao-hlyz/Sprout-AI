package top.ningmao.myspring.ai.huggingface;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import top.ningmao.myspring.ai.embedding.Embedding;
import top.ningmao.myspring.ai.embedding.EmbeddingModel;
import top.ningmao.myspring.ai.embedding.EmbeddingRequest;
import top.ningmao.myspring.ai.embedding.EmbeddingResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HuggingFaceEmbeddingModel - HuggingFace 嵌入向量模型实现
 * <p>
 * 使用 HuggingFace Lightweight Embeddings API 将文本转换为向量表示
 * API 文档：https://lamhieu-lightweight-embeddings.hf.space/
 * <p>
 * 支持多个轻量级模型，免费使用，无需 API Key
 * 
 * @author 宁猫
 * @since 2025-12-02
 */
public class HuggingFaceEmbeddingModel implements EmbeddingModel {
    
    /**
     * HuggingFace Lightweight Embeddings API 地址
     */
    private static final String API_URL = "https://lamhieu-lightweight-embeddings.hf.space/v1/embeddings";
    
    /**
     * 默认模型名称
     */
    private static final String DEFAULT_MODEL = "embeddinggemma-300m";
    
    /**
     * API 密钥（可选，HuggingFace 不需要）
     */
    private final String apiKey;
    
    /**
     * 模型名称
     */
    private final String model;
    
    /**
     * 构造函数（不需要 API Key）
     */
    public HuggingFaceEmbeddingModel() {
        this(null, DEFAULT_MODEL);
    }
    
    /**
     * 构造函数（指定模型）
     * 
     * @param model 模型名称
     */
    public HuggingFaceEmbeddingModel(String model) {
        this(null, model);
    }
    
    /**
     * 完整构造函数
     * 
     * @param apiKey API 密钥（
     * @param model 模型名称
     */
    public HuggingFaceEmbeddingModel(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model != null && !model.isBlank() ? model : DEFAULT_MODEL;
    }
    
    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        try {
            // 1. 构建请求体（OpenAI 兼容格式）
            JSONObject requestBody = buildRequestBody(request);
            
            // 2. 调用 API
            HttpRequest httpRequest = HttpRequest.post(API_URL)
                    .header("Content-Type", "application/json");
            
            // 如果提供了 API Key，添加 Authorization header
            if (apiKey != null && !apiKey.isBlank()) {
                httpRequest.header("Authorization", "Bearer " + apiKey);
            }
            
            HttpResponse response = httpRequest.body(requestBody.toString()).execute();
            
            // 3. 检查响应状态
            if (!response.isOk()) {
                throw new RuntimeException("HuggingFace Embedding API 调用失败: " + 
                        response.getStatus() + " - " + response.body());
            }
            
            // 4. 解析响应
            return parseResponse(response.body());
            
        } catch (Exception e) {
            throw new RuntimeException("调用 HuggingFace Embedding API 失败", e);
        }
    }
    
    /**
     * 构建请求体
     */
    private JSONObject buildRequestBody(EmbeddingRequest request) {
        JSONObject body = new JSONObject();
        
        // 设置模型
        String requestModel = request.getOptions().getModel();
        body.set("model", requestModel != null ? requestModel : this.model);
        
        // 设置输入文本（OpenAI 格式使用 "input"）
        List<String> inputs = request.getInputs();
        if (inputs.size() == 1) {
            // 单个文本直接传字符串
            body.set("input", inputs.get(0));
        } else {
            // 多个文本传数组
            body.set("input", inputs);
        }
        
        // 注意：HuggingFace Lightweight Embeddings 不支持自定义维度
        // 如果需要，可以取消注释以下代码
        // Integer dimensions = request.getOptions().getDimensions();
        // if (dimensions != null) {
        //     body.set("dimensions", dimensions);
        // }
        
        return body;
    }
    
    /**
     * 解析响应
     */
    private EmbeddingResponse parseResponse(String responseBody) {
        JSONObject json = JSONUtil.parseObj(responseBody);
        
        // 解析 embeddings 数组
        JSONArray dataArray = json.getJSONArray("data");
        List<Embedding> embeddings = new ArrayList<>();
        
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            
            // 获取嵌入向量
            JSONArray embeddingArray = item.getJSONArray("embedding");
            float[] vector = new float[embeddingArray.size()];
            for (int j = 0; j < embeddingArray.size(); j++) {
                vector[j] = embeddingArray.getFloat(j);
            }
            
            // 获取索引
            Integer index = item.getInt("index");
            
            // 创建 Embedding 对象
            embeddings.add(new Embedding(vector, index));
        }
        
        // 解析元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("model", json.getStr("model"));
        
        // 使用情况
        if (json.containsKey("usage")) {
            JSONObject usage = json.getJSONObject("usage");
            metadata.put("prompt_tokens", usage.getInt("prompt_tokens"));
            metadata.put("total_tokens", usage.getInt("total_tokens"));
        }
        
        return new EmbeddingResponse(embeddings, metadata);
    }
    
    /**
     * 获取模型名称
     */
    public String getModel() {
        return model;
    }
}
