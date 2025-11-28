package top.ningmao.myspring.ai.chat.memory;

import top.ningmao.myspring.ai.chat.messages.Message;

import java.util.List;


/**
 * 对话历史存储接口
 * 负责存储和检索对话消息
 *
 * @author 宁猫
 * @since 2025-11-28 14:00:10
 */
public interface ChatMemoryRepository {

    /**
     * 添加消息到指定对话
     *
     * @param conversationId 对话 ID
     * @param messages       要添加的消息列表
     */
    void add(String conversationId, List<Message> messages);

    /**
     * 获取指定对话的最近 N 条消息
     *
     * @param conversationId 对话 ID
     * @param lastN          获取最近 N 条消息，-1 表示获取全部
     * @return 消息列表
     */
    List<Message> get(String conversationId, int lastN);

    /**
     * 清除指定对话的所有消息
     *
     * @param conversationId 对话 ID
     */
    void clear(String conversationId);
}
