package top.ningmao.myspring.context;

import top.ningmao.myspring.bean.factory.HierarchicalBeanFactory;
import top.ningmao.myspring.bean.factory.ListableBeanFactory;
import top.ningmao.myspring.core.io.ResourceLoader;

/**
 * 应用上下文
 *
 * @author ningmao
 * @since 2025-5-10
 */
public interface ApplicationContext extends ListableBeanFactory , HierarchicalBeanFactory, ResourceLoader {
}
