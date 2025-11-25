package top.ningmao.myspring.ai.chat;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.model.MockChatModel;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * 简单的 ChatModel 测试
 *
 * @author 宁猫
 * @since 2025-11-25 15:28:13
 */
public class SimpleChatModelTest {

    @Test
    public void testMockChatModel() {
        // 创建 Mock ChatModel
        ChatModel chatModel = new MockChatModel("NingMao");

        // 调用
        String response = chatModel.call("你好，Sprout AI！");

        // 验证
        assertThat(response).isNotNull();
        assertThat(response).contains("你好，Sprout AI！");
        System.out.println("AI回复: " + response);
    }

}
