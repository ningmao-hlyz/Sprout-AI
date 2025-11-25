package top.ningmao.myspring.ai.model;

/**
 * ModelResult - 单个模型结果的基础类
 *
 * @author 宁猫
 * @since 2025-11-25 18:48:42
 */
public abstract class ModelResult<T> {

    /**
     * 获取模型输出
     *
     * @return 输出内容
     */
    public abstract T getOutput();
}
