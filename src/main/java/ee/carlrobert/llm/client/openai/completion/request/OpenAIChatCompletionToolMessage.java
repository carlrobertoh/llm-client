package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a tool result message in the conversation.
 * This message type is sent back to the model after executing a tool call.
 */
public class OpenAIChatCompletionToolMessage implements OpenAIChatCompletionMessage {

  private final String role = "tool";
  private final String content;
  private final String toolCallId;

  public OpenAIChatCompletionToolMessage(String content, String toolCallId) {
    this.content = content;
    this.toolCallId = toolCallId;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }

  @JsonProperty("tool_call_id")
  public String getToolCallId() {
    return toolCallId;
  }
}