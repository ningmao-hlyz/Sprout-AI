package top.ningmao.myspring.ai.embedding;

import java.util.HashMap;
import java.util.Map;

/**
 * Embedding - 单个嵌入向量结果
 * <p>
 * 代表将文本转换为向量后的结果，包含向量数据和元数据
 * 
 * @author 宁猫
 * @since 2025-12-02
 */
public class Embedding {
    
    /**
     * 嵌入向量（内部存储）
     */
    private final float[] embedding;
    
    /**
     * 索引（在批量请求中的位置）
     */
    private final Integer index;
    
    /**
     * 元数据（如模型信息、token 使用等）
     */
    private final Map<String, Object> metadata;
    
    /**
     * 构造函数
     * 
     * @param embedding 嵌入向量
     * @param index 索引
     */
    public Embedding(float[] embedding, Integer index) {
        this(embedding, index, new HashMap<>());
    }
    
    /**
     * 完整构造函数
     * 
     * @param embedding 嵌入向量
     * @param index 索引
     * @param metadata 元数据
     */
    public Embedding(float[] embedding, Integer index, Map<String, Object> metadata) {
        this.embedding = embedding != null ? embedding.clone() : null;
        this.index = index;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
    
    /**
     * 获取嵌入向量
     * <p>
     * 这是 ModelResult 接口的标准方法，返回模型输出结果
     * 
     * @return 嵌入向量
     */
    public float[] getOutput() {
        return embedding != null ? embedding.clone() : null;
    }
    
    /**
     * 获取嵌入向量（别名方法，保持语义清晰）
     * 
     * @return 嵌入向量
     */
    public float[] getEmbedding() {
        return getOutput();
    }
    
    /**
     * 获取向量维度
     */
    public int getDimension() {
        return embedding != null ? embedding.length : 0;
    }
    
    /**
     * 获取索引
     */
    public Integer getIndex() {
        return index;
    }
    
    /**
     * 获取元数据
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
        return "Embedding{" +
                "dimension=" + getDimension() +
                ", index=" + index +
                ", metadata=" + metadata +
                '}';
    }
}
