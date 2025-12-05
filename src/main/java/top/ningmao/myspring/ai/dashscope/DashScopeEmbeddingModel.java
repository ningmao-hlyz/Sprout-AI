package top.ningmao.myspring.ai.dashscope;

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
 * 阿里百炼（DashScope）Embedding 模型实现
 * <p>
 * 使用阿里云的文本向量化服务
 * API 文档：https://help.aliyun.com/zh/dashscope/developer-reference/text-embedding-api-details
 * 
 * @author 宁猫
 * @since 2025-12-04
 */
public class DashScopeEmbeddingModel implements EmbeddingModel {

    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings";
    
    /**
     * 默认模型：text-embedding-v2（通用文本向量模型）
     */
    private static final String DEFAULT_MODEL = "text-embedding-v1";
    
    private final String apiKey;
    private final String model;

    /**
     * 构造函数（使用默认模型）
     */
    public DashScopeEmbeddingModel(String apiKey) {
        this(apiKey, DEFAULT_MODEL);
    }

    /**
     * 构造函数（指定模型）
     */
    public DashScopeEmbeddingModel(String apiKey, String model) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("DashScope API Key 不能为空");
        }
        this.apiKey = apiKey;
        this.model = model != null && !model.isBlank() ? model : DEFAULT_MODEL;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        try {
            // 构建请求体（OpenAI 兼容格式）
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", this.model);
            
            // 兼容模式：input 直接是字符串或字符串数组，不是嵌套对象
            List<String> inputs = request.getInputs();
            if (inputs.size() == 1) {
                requestBody.set("input", inputs.get(0));
            } else {
                requestBody.set("input", inputs);
            }

            // 调用 API
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .execute();

            if (!response.isOk()) {
                throw new RuntimeException("DashScope API 调用失败: " +
                        response.getStatus() + " - " + response.body());
            }

            // 解析响应
            return parseResponse(response.body());

        } catch (Exception e) {
            throw new RuntimeException("调用 DashScope Embedding API 失败", e);
        }
    }

    /**
     * 解析 DashScope API 响应（OpenAI 兼容格式）
     */
    private EmbeddingResponse parseResponse(String responseBody) {
        JSONObject json = JSONUtil.parseObj(responseBody);

        // 检查错误（兼容模式使用 error 字段）
        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            String errorMsg = error.getStr("message", "未知错误");
            throw new RuntimeException("DashScope API 返回错误: " + errorMsg);
        }

        // 解析 embeddings（OpenAI 格式：data 数组）
        JSONArray dataArray = json.getJSONArray("data");

        List<Embedding> embeddings = new ArrayList<>();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject embeddingObj = dataArray.getJSONObject(i);
            
            // 获取向量
            JSONArray vectorArray = embeddingObj.getJSONArray("embedding");
            float[] vector = new float[vectorArray.size()];
            for (int j = 0; j < vectorArray.size(); j++) {
                vector[j] = vectorArray.getFloat(j);
            }
            
            // 获取索引（OpenAI 格式使用 index 字段）
            Integer index = embeddingObj.getInt("index");
            
            embeddings.add(new Embedding(vector, index));
        }

        // 构建元数据
        Map<String, Object> metadata = new HashMap<>();
        // OpenAI 格式：model 在顶层
        metadata.put("model", json.getStr("model", this.model));
        
        // 使用情况
        if (json.containsKey("usage")) {
            JSONObject usage = json.getJSONObject("usage");
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
