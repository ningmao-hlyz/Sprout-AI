package top.ningmao.myspring.ai.chat.memory;

import org.springframework.jdbc.core.JdbcTemplate;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * 基于 JDBC 的对话历史存储实现
 * 使用 Spring JdbcTemplate 操作数据库
 *
 * @author 宁猫
 * @since 2025-11-28 14:02:52
 */
public class JdbcChatMemoryRepository implements ChatMemoryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String tableName;

    public JdbcChatMemoryRepository(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, "sprout_ai_chat_memory");
    }

    public JdbcChatMemoryRepository(JdbcTemplate jdbcTemplate, String tableName) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
        initializeSchema();
    }

    /**
     * 初始化数据库表结构
     */
    private void initializeSchema() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS %s (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                conversation_id VARCHAR(255) NOT NULL,
                message_type VARCHAR(50) NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_conversation_id (conversation_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """.formatted(tableName);
        
        jdbcTemplate.execute(createTableSql);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (conversationId == null || messages == null || messages.isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO " + tableName + " (conversation_id, message_type, content) VALUES (?, ?, ?)";

        for (Message message : messages) {
            jdbcTemplate.update(insertSql,
                    conversationId,
                    message.getMessageType().getValue(),
                    message.getContent());
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null) {
            return new ArrayList<>();
        }

        String sql;
        if (lastN < 0) {
            // 获取全部
            sql = "SELECT message_type, content FROM " + tableName + 
                  " WHERE conversation_id = ? ORDER BY id ASC";
        } else {
            // 获取最近 N 条
            sql = "SELECT message_type, content FROM " + tableName + 
                  " WHERE conversation_id = ? ORDER BY id DESC LIMIT ?";
        }

        List<Message> messages = new ArrayList<>();

        if (lastN < 0) {
            jdbcTemplate.query(sql, rs -> {
                String type = rs.getString("message_type");
                String content = rs.getString("content");
                Message message = createMessage(type, content);
                if (message != null) {
                    messages.add(message);
                }
            }, conversationId);
        } else {
            jdbcTemplate.query(sql, rs -> {
                String type = rs.getString("message_type");
                String content = rs.getString("content");
                Message message = createMessage(type, content);
                if (message != null) {
                    messages.add(0, message);  // 倒序插入
                }
            }, conversationId, lastN);
        }

        return messages;
    }

    @Override
    public void clear(String conversationId) {
        if (conversationId == null) {
            return;
        }

        String deleteSql = "DELETE FROM " + tableName + " WHERE conversation_id = ?";
        jdbcTemplate.update(deleteSql, conversationId);
    }

    /**
     * 根据类型创建消息对象
     */
    private Message createMessage(String type, String content) {
        return switch (type) {
            case "system" -> new SystemMessage(content);
            case "user" -> new UserMessage(content);
            case "assistant" -> new AssistantMessage(content);
            default -> null;
        };
    }
}
