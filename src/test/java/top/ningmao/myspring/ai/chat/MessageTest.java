package top.ningmao.myspring.ai.chat;

import top.ningmao.myspring.ai.chat.messages.*;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashMap;
import java.util.Map;

/**
 * Message 测试
 *
 * @author 宁猫
 * @since 2025-11-25 17:40:16
 */
public class MessageTest {

    @Test
    public void testUserMessage() {
        Message message = new UserMessage("你好，AI！");

        assertThat(message.getContent()).isEqualTo("你好，AI！");
        assertThat(message.getMessageType()).isEqualTo(MessageType.USER);
        assertThat(message.getMetadata()).isNotNull();

        System.out.println(message);
    }

    @Test
    public void testSystemMessage() {
        Message message = new SystemMessage("你是一个有帮助的助手");

        assertThat(message.getContent()).isEqualTo("你是一个有帮助的助手");
        assertThat(message.getMessageType()).isEqualTo(MessageType.SYSTEM);

        System.out.println(message);
    }

    @Test
    public void testAssistantMessage() {
        Message message = new AssistantMessage("我很乐意帮助你！");

        assertThat(message.getContent()).isEqualTo("我很乐意帮助你！");
        assertThat(message.getMessageType()).isEqualTo(MessageType.ASSISTANT);

        System.out.println(message);
    }

    @Test
    public void testMessageWithMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("source", "test");

        Message message = new UserMessage("带元数据的消息", metadata);

        assertThat(message.getMetadata()).containsKey("timestamp");
        assertThat(message.getMetadata()).containsKey("source");
        assertThat(message.getMetadata().get("source")).isEqualTo("test");
    }
}
