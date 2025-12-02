package top.ningmao.myspring.ai.model;

import top.ningmao.myspring.ai.chat.model.ChatModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ChatModelFactory - AI 模型工厂
 * 管理多个 ChatModel 实例，支持动态切换
 *
 * @author 宁猫
 * @since 2025-12-02 16:36:50
 */
public class ChatModelFactory {

    /**
     * 存储模型实例的 Map
     * key: 模型名称, value: ChatModel 实例
     */
    private final Map<String, ChatModel> models = new HashMap<>();

    /**
     * 默认模型
     */
    private ChatModel defaultModel;

    /**
     * 自动注册模型（推荐）
     * 从 ChatModel.getModelName() 获取模型名称，无需手动指定
     *
     * @param model 模型实例
     * @return 模型名称
     */
    public String register(ChatModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        
        String modelName = model.getModelName();
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("Model name from getModelName() cannot be null or empty");
        }
        
        models.put(modelName, model);
        
        // 如果是第一个注册的模型，设为默认模型
        if (defaultModel == null) {
            defaultModel = model;
        }
        
        return modelName;
    }

    /**
     * 手动注册模型
     * 手动指定模型名称，不推荐使用，容易出现名称不一致的问题
     *
     * @param modelName 模型名称
     * @param model     模型实例
     */
    public void register(String modelName, ChatModel model) {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        
        models.put(modelName, model);
        
        // 如果是第一个注册的模型，设为默认模型
        if (defaultModel == null) {
            defaultModel = model;
        }
    }

    /**
     * 获取指定名称的模型
     *
     * @param modelName 模型名称
     * @return ChatModel 实例
     * @throws IllegalArgumentException 如果模型不存在
     */
    public ChatModel getModel(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        
        ChatModel model = models.get(modelName);
        if (model == null) {
            throw new IllegalArgumentException("Model not found: " + modelName + 
                    ". Available models: " + models.keySet());
        }
        
        return model;
    }

    /**
     * 获取默认模型
     *
     * @return 默认的 ChatModel 实例
     * @throws IllegalStateException 如果没有注册任何模型
     */
    public ChatModel getDefaultModel() {
        if (defaultModel == null) {
            throw new IllegalStateException("No models registered");
        }
        return defaultModel;
    }

    /**
     * 设置默认模型
     *
     * @param modelName 模型名称
     */
    public void setDefaultModel(String modelName) {
        ChatModel model = getModel(modelName);
        this.defaultModel = model;
    }

    /**
     * 检查模型是否存在
     *
     * @param modelName 模型名称
     * @return true 如果模型存在
     */
    public boolean hasModel(String modelName) {
        return models.containsKey(modelName);
    }

    /**
     * 获取所有已注册的模型名称
     *
     * @return 模型名称集合
     */
    public Set<String> getModelNames() {
        return models.keySet();
    }

    /**
     * 移除指定的模型
     *
     * @param modelName 模型名称
     * @return 被移除的模型，如果不存在则返回 null
     */
    public ChatModel remove(String modelName) {
        ChatModel removed = models.remove(modelName);
        
        // 如果移除的是默认模型，重新设置默认模型
        if (removed == defaultModel && !models.isEmpty()) {
            defaultModel = models.values().iterator().next();
        } else if (models.isEmpty()) {
            defaultModel = null;
        }
        
        return removed;
    }

    /**
     * 清空所有模型
     */
    public void clear() {
        models.clear();
        defaultModel = null;
    }

    /**
     * 获取已注册的模型数量
     *
     * @return 模型数量
     */
    public int size() {
        return models.size();
    }

    @Override
    public String toString() {
        return "ChatModelFactory{" +
                "models=" + models.keySet() +
                ", defaultModel=" + (defaultModel != null ? defaultModel.getClass().getSimpleName() : "null") +
                '}';
    }
}
