package top.ningmao.myspring.ai.model.function;


/**
 * 工具定义
 * 包含工具的名称、描述和输入参数的 JSON Schema
 *
 * @author 宁猫
 * @since 2025-11-26 17:27:08
 */
public class ToolDefinition {

    /**
     * 工具名称
     */
    private final String name;

    /**
     * 工具描述
     */
    private final String description;

    /**
     * 输入参数的 JSON Schema（描述工具接受的参数）
     */
    private final String inputSchema;

    public ToolDefinition(String name, String description, String inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }

    /**
     * 创建工具定义
     *
     * @param name        工具名称
     * @param description 工具描述
     * @param inputSchema JSON Schema
     * @return ToolDefinition
     */
    public static ToolDefinition of(String name, String description, String inputSchema) {
        return new ToolDefinition(name, description, inputSchema);
    }

    /**
     * 创建无参数工具定义
     *
     * @param name        工具名称
     * @param description 工具描述
     * @return ToolDefinition
     */
    public static ToolDefinition of(String name, String description) {
        return new ToolDefinition(name, description, "{}");
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String inputSchema() {
        return inputSchema;
    }

    @Override
    public String toString() {
        return "ToolDefinition{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", inputSchema='" + inputSchema + '\'' +
                '}';
    }
}
