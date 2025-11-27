package top.ningmao.myspring.ai.chat.prompt.template;

import top.ningmao.myspring.ai.chat.messages.Message;

import java.util.Map;


/**
 * Prompt Template 消息操作接口
 * 专注于通过生成和操作 Message 对象来创建提示词
 *
 * @author 宁猫
 * @since 2025-11-27 14:24:17
 */
public interface PromptTemplateMessageActions {

    /**
     * 创建消息（无变量）
     *
     * @return Message 对象
     */
    Message createMessage();

    /**
     * 创建消息（带变量）
     *
     * @param model 变量映射
     * @return Message 对象
     */
    Message createMessage(Map<String, Object> model);
}
