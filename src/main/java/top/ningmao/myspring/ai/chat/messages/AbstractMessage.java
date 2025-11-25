package top.ningmao.myspring.ai.chat.messages;

import java.util.HashMap;
import java.util.Map;

/**
 *消息抽象基类
 *
 * @author 宁猫
 * @since 2025-11-25 17:27:37
 */
public abstract class AbstractMessage implements Message {
    protected final String content;
    protected final Map<String, Object> metadata;

    protected AbstractMessage(String content) {
        this(content, new HashMap<>());
    }

    protected AbstractMessage(String content, Map<String, Object> metadata) {
        this.content = content;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return getMessageType().getValue() + ": " + content;
    }
}
