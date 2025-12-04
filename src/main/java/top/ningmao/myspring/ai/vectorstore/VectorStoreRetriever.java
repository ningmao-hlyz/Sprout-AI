package top.ningmao.myspring.ai.vectorstore;

import top.ningmao.myspring.ai.document.Document;

import java.util.List;

/**
 * VectorStoreRetriever - 向量存储检索接口（只读）
 * <p>
 * 提供只读的文档检索功能，用于相似性搜索
 * 遵循最小权限原则，只暴露检索能力，不包含修改操作
 * 
 * @author 宁猫
 * @since 2025-12-03
 */
@FunctionalInterface
public interface VectorStoreRetriever {
    
    /**
     * 根据搜索请求进行相似性搜索
     * 
     * @param request 搜索请求参数
     * @return 相似文档列表，按相似度降序排列
     */
    List<Document> similaritySearch(SearchRequest request);
    
    /**
     * 简化的相似性搜索（使用默认参数）
     * 
     * @param query 查询文本
     * @return 相似文档列表
     */
    default List<Document> similaritySearch(String query) {
        return similaritySearch(SearchRequest.builder().query(query).build());
    }
}
