package top.ningmao.myspring.ai.vectorstore;

/**
 * SearchRequest - 向量搜索请求
 * <p>
 * 封装向量相似性搜索的参数，包括查询文本、返回数量、相似度阈值等
 * 
 * @author 宁猫
 * @since 2025-12-03
 */
public class SearchRequest {
    
    /**
     * 接受所有结果的相似度阈值（0.0 = 不过滤）
     */
    public static final double SIMILARITY_THRESHOLD_ACCEPT_ALL = 0.0;
    
    /**
     * 默认返回的文档数量（Top K）
     */
    public static final int DEFAULT_TOP_K = 4;
    
    /**
     * 查询文本
     */
    private final String query;
    
    /**
     * 返回相似文档的最大数量（Top K）
     */
    private final int topK;
    
    /**
     * 相似度阈值（0.0-1.0），只返回相似度 >= 此值的文档
     */
    private final double similarityThreshold;
    
    /**
     * 私有构造函数，使用 Builder 创建
     */
    private SearchRequest(String query, int topK, double similarityThreshold) {
        this.query = query;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
    }
    
    /**
     * 创建 Builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 从现有 SearchRequest 创建 Builder（用于复制和修改）
     */
    public static Builder from(SearchRequest original) {
        return builder()
                .query(original.getQuery())
                .topK(original.getTopK())
                .similarityThreshold(original.getSimilarityThreshold());
    }
    
    /**
     * 获取查询文本
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * 获取 Top K
     */
    public int getTopK() {
        return topK;
    }
    
    /**
     * 获取相似度阈值
     */
    public double getSimilarityThreshold() {
        return similarityThreshold;
    }
    
    @Override
    public String toString() {
        return "SearchRequest{" +
                "query='" + query + '\'' +
                ", topK=" + topK +
                ", similarityThreshold=" + similarityThreshold +
                '}';
    }
    
    /**
     * SearchRequest Builder
     */
    public static class Builder {
        
        private String query = "";
        private int topK = DEFAULT_TOP_K;
        private double similarityThreshold = SIMILARITY_THRESHOLD_ACCEPT_ALL;
        
        private Builder() {
        }
        
        /**
         * 设置查询文本
         */
        public Builder query(String query) {
            if (query == null) {
                throw new IllegalArgumentException("查询文本不能为 null");
            }
            this.query = query;
            return this;
        }
        
        /**
         * 设置返回文档数量（Top K）
         */
        public Builder topK(int topK) {
            if (topK < 0) {
                throw new IllegalArgumentException("topK 必须 >= 0");
            }
            this.topK = topK;
            return this;
        }
        
        /**
         * 设置相似度阈值
         */
        public Builder similarityThreshold(double threshold) {
            if (threshold < 0 || threshold > 1) {
                throw new IllegalArgumentException("相似度阈值必须在 [0, 1] 范围内");
            }
            this.similarityThreshold = threshold;
            return this;
        }
        
        /**
         * 接受所有结果（相似度阈值 = 0.0）
         */
        public Builder similarityThresholdAll() {
            this.similarityThreshold = SIMILARITY_THRESHOLD_ACCEPT_ALL;
            return this;
        }
        
        /**
         * 构建 SearchRequest
         */
        public SearchRequest build() {
            return new SearchRequest(query, topK, similarityThreshold);
        }
    }
}
