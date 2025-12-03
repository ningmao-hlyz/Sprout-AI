package top.ningmao.myspring.ai.embedding;

/**
 * EmbeddingOptions - 嵌入向量配置选项
 * <p>
 * 配置 Embedding 模型的参数，如模型名称、向量维度等
 * 
 * @author 宁猫
 * @since 2025-12-02
 */
public class EmbeddingOptions {
    
    /**
     * 空选项（使用默认配置）
     */
    public static final EmbeddingOptions EMPTY = new EmbeddingOptions();
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 向量维度（某些模型支持自定义维度）
     */
    private Integer dimensions;
    
    /**
     * 默认构造函数
     */
    public EmbeddingOptions() {
    }
    
    /**
     * Builder 模式
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(Integer dimensions) {
        this.dimensions = dimensions;
    }
    
    @Override
    public String toString() {
        return "EmbeddingOptions{" +
                "model='" + model + '\'' +
                ", dimensions=" + dimensions +
                '}';
    }
    
    /**
     * Builder 类
     */
    public static class Builder {
        private final EmbeddingOptions options = new EmbeddingOptions();
        
        public Builder model(String model) {
            options.model = model;
            return this;
        }
        
        public Builder dimensions(Integer dimensions) {
            options.dimensions = dimensions;
            return this;
        }
        
        public EmbeddingOptions build() {
            return options;
        }
    }
}
