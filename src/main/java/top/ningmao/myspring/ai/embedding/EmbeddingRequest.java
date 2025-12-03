package top.ningmao.myspring.ai.embedding;

import java.util.ArrayList;
import java.util.List;

/**
 * EmbeddingRequest - 嵌入向量请求
 * <p>
 * 包含需要转换为向量的文本列表和可选的配置选项
 * 
 * @author 宁猫
 * @since 2025-12-02
 */
public class EmbeddingRequest {
    
    /**
     * 输入文本列表
     */
    private final List<String> inputs;
    
    /**
     * 嵌入选项（如模型名称、维度等）
     */
    private final EmbeddingOptions options;
    
    /**
     * 从单个文本创建请求
     * 
     * @param input 输入文本
     */
    public EmbeddingRequest(String input) {
        this(List.of(input), EmbeddingOptions.EMPTY);
    }
    
    /**
     * 从文本列表创建请求
     * 
     * @param inputs 输入文本列表
     */
    public EmbeddingRequest(List<String> inputs) {
        this(inputs, EmbeddingOptions.EMPTY);
    }
    
    /**
     * 完整构造函数
     * 
     * @param inputs 输入文本列表
     * @param options 嵌入选项
     */
    public EmbeddingRequest(List<String> inputs, EmbeddingOptions options) {
        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException("输入文本列表不能为空");
        }
        this.inputs = new ArrayList<>(inputs);
        this.options = options != null ? options : EmbeddingOptions.EMPTY;
    }
    
    /**
     * 获取输入文本列表
     */
    public List<String> getInputs() {
        return new ArrayList<>(inputs);
    }
    
    /**
     * 获取嵌入选项
     */
    public EmbeddingOptions getOptions() {
        return options;
    }
    
    @Override
    public String toString() {
        return "EmbeddingRequest{" +
                "inputs=" + inputs.size() + " texts" +
                ", options=" + options +
                '}';
    }
}
