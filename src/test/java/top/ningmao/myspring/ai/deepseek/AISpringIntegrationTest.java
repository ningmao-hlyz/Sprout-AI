package top.ningmao.myspring.ai.deepseek;

import org.junit.jupiter.api.Test;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import top.ningmao.myspring.ai.chat.model.ChatModel;
import top.ningmao.myspring.ai.chat.model.ChatResponse;
import top.ningmao.myspring.ai.chat.prompt.Prompt;
import top.ningmao.myspring.context.support.ClassPathXmlApplicationContext;


/**
 * DeepSeek 接入演示
 *
 * @author 宁猫
 * @since 2025-11-25 21:06:24
 */
public class AISpringIntegrationTest {

    /**
     * 测试从 Spring 容器获取 ChatModel Bean
     */
    @Test
    public void testGetChatModelFromSpring() {
        // 1. 创建 Spring 容器，加载 AI 配置
        ClassPathXmlApplicationContext applicationContext = 
            new ClassPathXmlApplicationContext("classpath:ai-config.xml");

        // 2. 从容器获取 ChatModel Bean
        ChatModel chatModel = applicationContext.getBean("chatModel", ChatModel.class);

        System.out.println("从 Spring 容器获取的 ChatModel: " + chatModel.getClass().getSimpleName());
    }

    /**
     * 测试使用 Spring 管理的 ChatModel 进行调用
     */
    @Test
    public void testCallWithSpringManagedChatModel() {
        // 从 Spring 容器获取 ChatModel
        ClassPathXmlApplicationContext applicationContext = 
            new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        ChatModel chatModel = applicationContext.getBean("chatModel", ChatModel.class);

        // 调用 AI
        String response = chatModel.call("你好，请用一句话介绍你自己");
        
        System.out.println("AI 回复: " + response);
    }

    /**
     * 测试带系统提示词的调用
     */
    @Test
    public void testCallWithSystemMessage() {
        ClassPathXmlApplicationContext applicationContext = 
            new ClassPathXmlApplicationContext("classpath:ai-config.xml");
        ChatModel chatModel = applicationContext.getBean("chatModel", ChatModel.class);

        // 创建 Prompt
        Prompt prompt = new Prompt(
            new SystemMessage("你是一个 Java 专家，请用简洁专业的语言回答"),
            new UserMessage("什么是 Spring IoC？")
        );

        // 调用
        ChatResponse response = chatModel.call(prompt);
        System.out.println("AI 回复: " + response.getOutput());
    }

}
