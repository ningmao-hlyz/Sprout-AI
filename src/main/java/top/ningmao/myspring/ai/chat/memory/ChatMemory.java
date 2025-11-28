package top.ningmao.myspring.ai.chat.memory;

import top.ningmao.myspring.ai.chat.messages.Message;

import java.util.List;


/**
 * 对话历史管理接口
 * 负责管理对话消息的添加、获取和清除
 *
 * @author 宁猫
 * @since 2025-11-28 13:57:02
 */
public interface ChatMemory {

    /**
     * 对话 ID 参数键
     */
    String CONVERSATION_ID = "conversationId";

    /**
     * 添加消息到指定对话
     *
     * @param conversationId 对话 ID
     * @param messages       要添加的消息列表
     */
    void add(String conversationId, List<Message> messages);

    /**
     * 获取指定对话的消息历史
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
