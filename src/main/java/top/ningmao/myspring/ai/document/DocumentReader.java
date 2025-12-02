package top.ningmao.myspring.ai.document;

import java.util.List;

/**
 * DocumentReader - 文档读取器接口
 * 职责：从不同来源加载文档
 * - 文本文件
 * - PDF文件
 * - Web页面
 * - 数据库等
 *
 * @author 宁猫
 * @since 2025-12-02
 */
public interface DocumentReader {

    /**
     * 读取文档
     * 可能返回多个文档（如分页、分块等）
     *
     * @return 文档列表
     */
    List<Document> read();
}
