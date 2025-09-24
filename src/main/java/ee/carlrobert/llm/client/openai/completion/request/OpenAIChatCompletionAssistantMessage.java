package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.client.openai.completion.response.ToolCall;
import java.util.List;

/**
 * Represents an assistant message in the conversation.
 * This message type can include both content and tool calls.
 */
public class OpenAIChatCompletionAssistantMessage implements OpenAIChatCompletionMessage {

  private final String role = "assistant";
  private final String content;
  private final List<ToolCall> toolCalls;

  public OpenAIChatCompletionAssistantMessage(String content) {
    this(content, null);
  }

  public OpenAIChatCompletionAssistantMessage(String content, List<ToolCall> toolCalls) {
    this.content = content;
    this.toolCalls = toolCalls;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }

  @JsonProperty("tool_calls")
  public List<ToolCall> getToolCalls() {
    return toolCalls;
  }
}