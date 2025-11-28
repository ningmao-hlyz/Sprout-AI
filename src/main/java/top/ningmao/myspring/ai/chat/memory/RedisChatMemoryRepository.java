package top.ningmao.myspring.ai.chat.memory;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import top.ningmao.myspring.ai.chat.messages.AssistantMessage;
import top.ningmao.myspring.ai.chat.messages.Message;
import top.ningmao.myspring.ai.chat.messages.MessageType;
import top.ningmao.myspring.ai.chat.messages.SystemMessage;
import top.ningmao.myspring.ai.chat.messages.UserMessage;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;


/**
 * 基于 Redis 的对话历史存储实现
 * 使用 Jedis 客户端
 *
 * @author 宁猫
 * @since 2025-11-28 14:02:35
 */
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private final JedisPool jedisPool;
    private final String keyPrefix;

    public RedisChatMemoryRepository(JedisPool jedisPool) {
        this(jedisPool, "chat:memory:");
    }

    public RedisChatMemoryRepository(JedisPool jedisPool, String keyPrefix) {
        this.jedisPool = jedisPool;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (conversationId == null || messages == null || messages.isEmpty()) {
            return;
        }

        String key = keyPrefix + conversationId;

        try (Jedis jedis = jedisPool.getResource()) {
            for (Message message : messages) {
                JSONObject json = new JSONObject();
                json.set("type", message.getMessageType().getValue());
                json.set("content", message.getContent());
                jedis.rpush(key, json.toString());
            }
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null) {
            return new ArrayList<>();
        }

        String key = keyPrefix + conversationId;

        try (Jedis jedis = jedisPool.getResource()) {
            long len = jedis.llen(key);
            if (len == 0) {
                return new ArrayList<>();
            }

            // 获取全部
            long start = 0;
            long end = -1;

            // 获取最近 N 条
            if (lastN > 0 && lastN < len) {
                start = len - lastN;
            }

            List<String> jsonList = jedis.lrange(key, start, end);
            List<Message> messages = new ArrayList<>();

            for (String jsonStr : jsonList) {
                JSONObject json = JSONUtil.parseObj(jsonStr);
                String type = json.getStr("type");
                String content = json.getStr("content");

                Message message = switch (type) {
                    case "system" -> new SystemMessage(content);
                    case "user" -> new UserMessage(content);
                    case "assistant" -> new AssistantMessage(content);
                    default -> null;
                };

                if (message != null) {
                    messages.add(message);
                }
            }

            return messages;
        }
    }

    @Override
    public void clear(String conversationId) {
        if (conversationId == null) {
            return;
        }

        String key = keyPrefix + conversationId;
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
}
