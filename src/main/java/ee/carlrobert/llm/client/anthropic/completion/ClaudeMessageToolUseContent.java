package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Map;

/**
 * Represents a tool use request in Claude message content.
 * This class is used when the model requests to use a tool during the completion.
 */
@JsonTypeName("tool_use")
public class ClaudeMessageToolUseContent extends ClaudeMessageContent {

  private String id;
  private String name;
  private Map<String, Object> input;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Object> getInput() {
    return input;
  }

  public void setInput(Map<String, Object> input) {
    this.input = input;
  }
}
