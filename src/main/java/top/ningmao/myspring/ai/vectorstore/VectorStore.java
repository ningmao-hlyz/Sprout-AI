package top.ningmao.myspring.ai.vectorstore;

import top.ningmao.myspring.ai.document.Document;

import java.util.List;

/**
 * VectorStore - 向量存储接口
 * <p>
 * 提供完整的向量数据库操作能力，包括：
 * - 添加文档（自动生成向量）
 * - 删除文档
 * - 相似性搜索
 * 
 * @author 宁猫
 * @since 2025-12-03
 */
public interface VectorStore extends VectorStoreRetriever {
    
    /**
     * 获取向量存储名称
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * 添加文档列表到向量存储
     * <p>
     * 会自动为文档生成向量嵌入并存储
     * 
     * @param documents 文档列表
     * @throws IllegalArgumentException 如果文档列表为空或包含重复 ID
     */
    void add(List<Document> documents);
    
    /**
     * 根据 ID 列表删除文档
     * 
     * @param idList 文档 ID 列表
     */
    void delete(List<String> idList);
    
    /**
     * 获取向量存储中的文档总数
     */
    default int size() {
        throw new UnsupportedOperationException("此实现不支持 size() 方法");
    }
}
