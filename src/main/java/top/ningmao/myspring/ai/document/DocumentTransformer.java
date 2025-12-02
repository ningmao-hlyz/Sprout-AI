package top.ningmao.myspring.ai.document;

import java.util.List;
import java.util.function.Function;

/**
 * DocumentTransformer - 文档转换器接口
 * <p>
 * 职责：对文档列表进行转换处理
 * - 文本分块（Splitting）
 * - 元数据增强（Enrichment）
 * - 内容格式化（Formatting）
 *
 * @author 宁猫
 * @since 2025-12-02
 */
@FunctionalInterface
public interface DocumentTransformer extends Function<List<Document>, List<Document>> {

    /**
     * 转换文档列表
     *
     * @param documents 原始文档列表
     * @return 转换后的文档列表
     */
    @Override
    List<Document> apply(List<Document> documents);
}
