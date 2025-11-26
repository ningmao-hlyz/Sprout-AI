package top.ningmao.myspring.ai;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.model.StreamingChatModel;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;

/**
 * 流式响应测试
 * 演示 DeepSeek ChatModel 的流式输出功能
 *
 * @author 宁猫
 * @since 2025-11-26
 */
public class StreamingChatModelTest {

    /**
     * 测试基本的流式响应
     */
    @Test
    public void testBasicStreaming() {
        // 1. 从 Spring 容器获取 ChatModel
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        StreamingChatModel chatModel = context.getBean("chatModel", StreamingChatModel.class);

        System.out.println("===== 开始流式响应 =====");
        System.out.print("AI: ");

        // 2. 流式调用，逐字输出
        chatModel.stream("请用200个字介绍Spring IoC 容器", chunk -> {
            System.out.print(chunk);  // 逐字打印
        });

        System.out.println("\n===== 流式响应结束 =====");
    }

    /**
     * 测试带系统提示词的流式响应
     */
    @Test
    public void testStreamingWithSystemMessage() {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        StreamingChatModel chatModel = context.getBean("chatModel", StreamingChatModel.class);

        System.out.println("===== 开始流式响应（带系统提示词）=====");
        System.out.print("AI: ");

        // 创建带系统提示词的 Prompt
        Prompt prompt = new Prompt(
                new SystemMessage("你是一个诗人，请用优美的语言回答"),
                new UserMessage("描述一下春天")
        );

        chatModel.stream(prompt, chunk -> {
            System.out.print(chunk);
        });

        System.out.println("\n===== 流式响应结束 =====");
    }


}
