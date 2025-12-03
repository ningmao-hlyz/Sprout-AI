package top.ningmao.myspring.ai.embedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EmbeddingResponse - 嵌入向量响应
 * <p>
 * 包含嵌入向量结果列表和响应元数据
 * 
 * @author 宁猫
 * @since 2025-12-02
 */
public class EmbeddingResponse {
    
    /**
     * 嵌入向量结果列表
     */
    private final List<Embedding> results;
    
    /**
     * 响应元数据（如使用的 token 数量、模型信息等）
     */
    private final Map<String, Object> metadata;
    
    /**
     * 构造函数
     * 
     * @param results 嵌入向量结果列表
     */
    public EmbeddingResponse(List<Embedding> results) {
        this(results, new HashMap<>());
    }
    
    /**
     * 完整构造函数
     * 
     * @param results 嵌入向量结果列表
     * @param metadata 响应元数据
     */
    public EmbeddingResponse(List<Embedding> results, Map<String, Object> metadata) {
        this.results = results != null ? new ArrayList<>(results) : new ArrayList<>();
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    /**
     * 获取嵌入向量结果列表
     */
    public List<Embedding> getResults() {
        return new ArrayList<>(results);
    }
    
    /**
     * 获取响应元数据
     */
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    /**
     * 获取特定的元数据
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    @Override
    public String toString() {
        return "EmbeddingResponse{" +
                "results=" + results.size() + " embeddings" +
                ", metadata=" + metadata +
                '}';
    }
}
