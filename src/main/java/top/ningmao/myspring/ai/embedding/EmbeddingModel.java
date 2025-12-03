package top.ningmao.myspring.ai.embedding;

import top.ningmao.myspring.ai.document.Document;

import java.util.List;

/**
 * EmbeddingModel - 嵌入向量模型接口
 * <p>
 * 参考 Spring AI 的 EmbeddingModel 设计，定义标准的文本向量化接口
 * <p>
 * Spring AI 对应接口：org.springframework.ai.embedding.EmbeddingModel
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public interface EmbeddingModel {
    
    /**
     * 核心方法：处理嵌入请求
     * 
     * @param request 嵌入请求
     * @return 嵌入响应
     */
    EmbeddingResponse call(EmbeddingRequest request);
    
    /**
     * 将单个文本转换为嵌入向量
     * 
     * @param text 文本内容
     * @return 嵌入向量
     */
    default float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("文本不能为空");
        }
        EmbeddingResponse response = call(new EmbeddingRequest(text));
        return response.getResults().get(0).getEmbedding();
    }
    
    /**
     * 将文档转换为嵌入向量
     * 
     * @param document 文档对象
     * @return 嵌入向量
     */
    default float[] embed(Document document) {
        if (document == null || document.getContent() == null) {
            throw new IllegalArgumentException("文档内容不能为空");
        }
        return embed(document.getContent());
    }
    
    /**
     * 批量将文本转换为嵌入向量
     * 
     * @param texts 文本列表
     * @return 嵌入向量列表
     */
    default List<float[]> embed(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("文本列表不能为空");
        }
        EmbeddingResponse response = call(new EmbeddingRequest(texts));
        return response.getResults().stream()
                .map(Embedding::getOutput)  // 使用 Spring AI 标准方法
                .toList();
    }
    
    /**
     * 批量嵌入并返回完整响应（包含元数据）
     * 
     * @param texts 文本列表
     * @return 完整的嵌入响应
     */
    default EmbeddingResponse embedForResponse(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("文本列表不能为空");
        }
        return call(new EmbeddingRequest(texts));
    }
    
    /**
     * 获取嵌入向量的维度
     * 
     * @return 向量维度
     */
    default int dimensions() {
        return embed("测试文本").length;
    }
}
