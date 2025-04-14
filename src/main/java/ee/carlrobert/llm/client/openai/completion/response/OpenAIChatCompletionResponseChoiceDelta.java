package ee.carlrobert.llm.client.openai.completion.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIChatCompletionResponseChoiceDelta {

  private final String role;
  private final String content;
  private final String reasoningContent;
  private final List<ToolCall> toolCalls;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public OpenAIChatCompletionResponseChoiceDelta(
      @JsonProperty("role") String role,
      @JsonProperty("content") String content,
      @JsonProperty("reasoning_content") String reasoningContent,
      @JsonProperty("tool_calls") List<ToolCall> toolCalls) {
    this.role = role;
    this.content = content;
    this.toolCalls = toolCalls;
    this.reasoningContent = reasoningContent;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }

  public List<ToolCall> getToolCalls() {
    return toolCalls;
  }

  public String getReasoningContent() {
    return reasoningContent;
  }
}
