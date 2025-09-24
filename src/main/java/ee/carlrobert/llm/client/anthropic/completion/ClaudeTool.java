package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a tool definition for Claude completions.
 * Tools allow the model to call external functions during the completion process.
 */
public class ClaudeTool {
  private String name;
  private String description;
  @JsonProperty("input_schema")
  private Map<String, Object> inputSchema;

  /**
   * Default constructor for JSON deserialization.
   */
  public ClaudeTool() {
  }

  /**
   * Creates a new Claude tool with the specified parameters.
   *
   * @param name The tool name (required)
   * @param description The tool description (required)
   * @param inputSchema The JSON schema for tool input parameters (required)
   * @throws IllegalArgumentException if any required parameter is null
   */
  public ClaudeTool(String name, String description, Map<String, Object> inputSchema) {
    this.name = Objects.requireNonNull(name, "Tool name cannot be null");
    this.description = Objects.requireNonNull(description, "Tool description cannot be null");
    this.inputSchema = Objects.requireNonNull(inputSchema, "Tool input schema cannot be null");
  }

  /**
   * Gets the tool name.
   *
   * @return The tool name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the tool name.
   *
   * @param name The tool name
   * @throws IllegalArgumentException if name is null
   */
  public void setName(String name) {
    this.name = Objects.requireNonNull(name, "Tool name cannot be null");
  }

  /**
   * Gets the tool description.
   *
   * @return The tool description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the tool description.
   *
   * @param description The tool description
   * @throws IllegalArgumentException if description is null
   */
  public void setDescription(String description) {
    this.description = Objects.requireNonNull(description, "Tool description cannot be null");
  }

  /**
   * Gets the input schema for the tool.
   *
   * @return The input schema map
   */
  public Map<String, Object> getInputSchema() {
    return inputSchema;
  }

  /**
   * Sets the input schema for the tool.
   *
   * @param inputSchema The input schema map
   * @throws IllegalArgumentException if inputSchema is null
   */
  public void setInputSchema(Map<String, Object> inputSchema) {
    this.inputSchema = Objects.requireNonNull(inputSchema, "Tool input schema cannot be null");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ClaudeTool that = (ClaudeTool) obj;
    return Objects.equals(name, that.name)
        && Objects.equals(description, that.description)
        && Objects.equals(inputSchema, that.inputSchema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, inputSchema);
  }

  @Override
  public String toString() {
    return "ClaudeTool{"
        + "name='" + name + '\''
        + ", description='" + description + '\''
        + ", inputSchema=" + inputSchema
        + '}';
  }
}