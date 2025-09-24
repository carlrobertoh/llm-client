package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.llm.completion.CompletionResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeCompletionResponse implements CompletionResponse {

  private String id;
  private String type;
  private String role;
  private List<ClaudeCompletionResponseMessage> content;
  private ClaudeCompletionResponseUsage usage;
  private String stopReason;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public List<ClaudeCompletionResponseMessage> getContent() {
    return content;
  }

  public void setContent(List<ClaudeCompletionResponseMessage> content) {
    this.content = content;
  }

  public ClaudeCompletionResponseUsage getUsage() {
    return usage;
  }

  public void setUsage(ClaudeCompletionResponseUsage usage) {
    this.usage = usage;
  }

  public String getStopReason() {
    return stopReason;
  }

  public void setStopReason(String stopReason) {
    this.stopReason = stopReason;
  }
}
