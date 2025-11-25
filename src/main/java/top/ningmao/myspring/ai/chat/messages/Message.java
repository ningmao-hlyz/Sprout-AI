package top.ningmao.myspring.ai.chat.messages;

import java.util.Map;

/**
 * 消息接口 - 对话中的基本单元
 *
 * @author 宁猫
 * @since 2025-11-25 17:26:50
 */
public interface Message {

    /**
     * 获取消息内容
     *
     * @return 消息的文本内容
     */
    String getContent();

    /**
     * 获取消息类型（角色）
     *
     * @return 消息类型枚举
     */
    MessageType getMessageType();

    /**
     * 获取元数据
     * <p>
     * 元数据可以包含额外信息，如时间戳、来源等
     *
     * @return 元数据 Map
     */
    Map<String, Object> getMetadata();
}
