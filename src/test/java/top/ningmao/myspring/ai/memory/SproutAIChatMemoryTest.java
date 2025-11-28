package top.ningmao.myspring.ai.memory;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.JedisPool;
import top.ningmao.myspring.ai.chat.memory.*;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * SproutAI Chat Memory 测试
 * 测试三种存储方式：InMemory、Redis、MySQL
 *
 * @author 宁猫
 * @since 2025-11-28 14:02:59
 */
public class SproutAIChatMemoryTest {

    // ========== 1. InMemory 存储测试 ==========

    @Test
    public void testInMemoryStorage() {
        System.out.println("\n===== 测试 InMemory 存储 =====");
        
        ChatMemory memory = MessageWindowChatMemory.builder()
                .repository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();

        String conversationId = "test-conversation-inmemory";

        // 添加消息
        memory.add(conversationId, List.of(
                new SystemMessage("你是一个helpful助手"),
                new UserMessage("你好"),
                new AssistantMessage("你好！有什么我可以帮助你的吗？")
        ));

        // 验证消息
        List<Message> messages = memory.get(conversationId, -1);
        assertThat(messages).hasSize(3);
        assertThat(messages.get(0).getContent()).isEqualTo("你是一个helpful助手");
        assertThat(messages.get(1).getContent()).isEqualTo("你好");
        assertThat(messages.get(2).getContent()).isEqualTo("你好！有什么我可以帮助你的吗？");

        // 测试消息窗口限制
        for (int i = 1; i <= 10; i++) {
            memory.add(conversationId, List.of(
                    new UserMessage("问题 " + i),
                    new AssistantMessage("回答 " + i)
            ));
        }

        messages = memory.get(conversationId, -1);
        assertThat(messages.size()).isLessThanOrEqualTo(10);
        assertThat(messages.get(0).getContent()).isEqualTo("你是一个helpful助手");  // 系统消息保留

        // 测试获取最近 N 条
        memory.clear(conversationId);
        memory.add(conversationId, List.of(
                new UserMessage("消息1"),
                new AssistantMessage("回复1"),
                new UserMessage("消息2"),
                new AssistantMessage("回复2")
        ));

        List<Message> last2 = memory.get(conversationId, 2);
        assertThat(last2).hasSize(2);
        assertThat(last2.get(0).getContent()).isEqualTo("消息2");
        assertThat(last2.get(1).getContent()).isEqualTo("回复2");

        System.out.println(" InMemory 存储测试通过");
    }

    // ========== 2. Redis 存储测试 ==========

    @Test
    public void testRedisStorage() {
        System.out.println("\n===== 测试 Redis 存储 =====");
        
        JedisPool jedisPool = new JedisPool("localhost", 6379);
        
        ChatMemory memory = MessageWindowChatMemory.builder()
                .repository(new RedisChatMemoryRepository(jedisPool))
                .maxMessages(10)
                .build();

        String conversationId = "test-conversation-redis";

        // 清除旧数据
        memory.clear(conversationId);

        // 添加消息
        memory.add(conversationId, List.of(
                new SystemMessage("你是一个helpful助手"),
                new UserMessage("你好"),
                new AssistantMessage("你好！有什么我可以帮助你的吗？")
        ));

        // 验证消息
        List<Message> messages = memory.get(conversationId, -1);
        assertThat(messages).hasSize(3);
        assertThat(messages.get(0).getContent()).isEqualTo("你是一个helpful助手");

        // 测试消息窗口
        memory.add(conversationId, List.of(new SystemMessage("你是助手")));
        for (int i = 1; i <= 10; i++) {
            memory.add(conversationId, List.of(
                    new UserMessage("问题 " + i),
                    new AssistantMessage("回答 " + i)
            ));
        }

        messages = memory.get(conversationId, -1);
        assertThat(messages.size()).isLessThanOrEqualTo(10);

        System.out.println(" Redis 存储测试通过（数据已保存到 Redis）");
    }

    // ========== 3. MySQL 存储测试 ==========

    @Test
    public void testMySQLStorage() {
        System.out.println("\n===== 测试 MySQL 存储 =====");
        
        // 创建数据库
        MysqlDataSource bootstrapDataSource = new MysqlDataSource();
        bootstrapDataSource.setURL("jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        bootstrapDataSource.setUser("root");
        bootstrapDataSource.setPassword("123456");

        try {
            JdbcTemplate bootstrapTemplate = new JdbcTemplate(bootstrapDataSource);
            bootstrapTemplate.execute("CREATE DATABASE IF NOT EXISTS sprout_ai_test DEFAULT CHARACTER SET utf8mb4");
        } catch (Exception e) {
            System.err.println("创建数据库失败: " + e.getMessage());
        }

        // 连接数据库
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/sprout_ai_test?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        dataSource.setUser("root");
        dataSource.setPassword("123456");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        ChatMemory memory = MessageWindowChatMemory.builder()
                .repository(new JdbcChatMemoryRepository(jdbcTemplate))
                .maxMessages(10)
                .build();

        String conversationId = "test-conversation-mysql";

        // 清除旧数据
        memory.clear(conversationId);

        // 添加消息
        memory.add(conversationId, List.of(
                new SystemMessage("你是一个helpful助手"),
                new UserMessage("你好"),
                new AssistantMessage("你好！有什么我可以帮助你的吗？")
        ));

        // 验证消息
        List<Message> messages = memory.get(conversationId, -1);
        assertThat(messages).hasSize(3);
        assertThat(messages.get(0).getContent()).isEqualTo("你是一个helpful助手");
        assertThat(messages.get(1).getContent()).isEqualTo("你好");

        // 测试消息窗口
        for (int i = 1; i <= 10; i++) {
            memory.add(conversationId, List.of(
                    new UserMessage("问题 " + i),
                    new AssistantMessage("回答 " + i)
            ));
        }

        messages = memory.get(conversationId, -1);
        assertThat(messages.size()).isLessThanOrEqualTo(10);
        assertThat(messages.get(0).getContent()).isEqualTo("你是一个helpful助手");  // 系统消息保留

        // 测试获取最近 N 条
        memory.clear(conversationId);
        memory.add(conversationId, List.of(
                new UserMessage("消息1"),
                new AssistantMessage("回复1"),
                new UserMessage("消息2"),
                new AssistantMessage("回复2"),
                new UserMessage("消息3"),
                new AssistantMessage("回复3")
        ));

        List<Message> last2 = memory.get(conversationId, 2);
        assertThat(last2).hasSize(2);
        assertThat(last2.get(0).getContent()).isEqualTo("消息3");
        assertThat(last2.get(1).getContent()).isEqualTo("回复3");

        System.out.println(" MySQL 存储测试通过（数据已持久化到 MySQL）");
    }


}
