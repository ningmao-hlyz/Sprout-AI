package top.ningmao.myspring.ai.model;


/**
 * ModelRequest - 模型请求的基础接口
 *
 * @author 宁猫
 * @since 2025-11-25 18:43:13
 */
public interface ModelRequest<I> {

    /**
     * 获取模型的输入指令
     *
     * @return 指令内容
     */
    I getInstructions();

    /**
     * 获取模型配置选项
     *
     * @return 配置选项
     */
    ModelOptions getOptions();
}
