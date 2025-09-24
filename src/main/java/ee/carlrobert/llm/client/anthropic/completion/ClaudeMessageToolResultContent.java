package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents the result of a tool execution in Claude message content.
 * This class is used to convey the output of a tool call back to the model.
 */
@JsonTypeName("tool_result")
public class ClaudeMessageToolResultContent extends ClaudeMessageContent {

  @JsonProperty("tool_use_id")
  private String toolUseId;
  private String content;

  public String getToolUseId() {
    return toolUseId;
  }

  public void setToolUseId(String toolUseId) {
    this.toolUseId = toolUseId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
