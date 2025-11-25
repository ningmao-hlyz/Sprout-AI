package top.ningmao.myspring.ai.chat.model;


import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.model.ModelResult;

/**
 * Generation - 单个 AI 生成结果
 * 代表 AI 模型的一次生成输出
 *
 * @author 宁猫
 * @since 2025-11-25 18:50:08
 */
public class Generation extends ModelResult<AssistantMessage> {

    private final AssistantMessage assistantMessage;

    /**
     * 从文本创建 Generation
     */
    public Generation(String text) {
        this(new AssistantMessage(text));
    }

    /**
     * 从 AssistantMessage 创建 Generation
     */
    public Generation(AssistantMessage assistantMessage) {
        this.assistantMessage = assistantMessage;
    }

    @Override
    public AssistantMessage getOutput() {
        return assistantMessage;
    }

    @Override
    public String toString() {
        return "Generation{" +
                "output=" + assistantMessage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Generation that = (Generation) o;
        return assistantMessage != null ? assistantMessage.equals(that.assistantMessage) : that.assistantMessage == null;
    }

    @Override
    public int hashCode() {
        return assistantMessage != null ? assistantMessage.hashCode() : 0;
    }

}
