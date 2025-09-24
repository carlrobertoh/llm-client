package ee.carlrobert.llm.client.anthropic.completion;

import java.util.Objects;

/**
 * Represents a tool choice configuration for Claude completions.
 * This class specifies how the model should handle tool usage in responses.
 */
public class ClaudeToolChoice {

  private final String type;
  private final String name;

  public ClaudeToolChoice(String type, String name) {
    this.type = Objects.requireNonNull(type, "Type cannot be null");
    
    if ("tool".equals(type) && name == null) {
      throw new IllegalArgumentException("Tool name is required when type is 'tool'");
    }
    
    this.name = name;
  }

  public static ClaudeToolChoice auto() {
    return new ClaudeToolChoice("auto", null);
  }

  public static ClaudeToolChoice any() {
    return new ClaudeToolChoice("any", null);
  }

  public static ClaudeToolChoice tool(String toolName) {
    return new ClaudeToolChoice("tool", toolName);
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ClaudeToolChoice that = (ClaudeToolChoice) obj;
    return Objects.equals(type, that.type) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  @Override
  public String toString() {
    return "ClaudeToolChoice{"
        + "type='" + type + '\''
        + ", name='" + name + '\''
        + '}';
  }
}