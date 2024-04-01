package ee.carlrobert.llm.client.anthropic.completion;

import java.util.Collections;
import java.util.List;

public class ClaudeCompletionDetailedMessage implements ClaudeCompletionMessage {

  private String role;
  private List<ClaudeMessageContent> content;

  public ClaudeCompletionDetailedMessage(String role, ClaudeMessageContent content) {
    this.role = role;
    this.content = Collections.singletonList(content);
  }

  public ClaudeCompletionDetailedMessage(String role, List<ClaudeMessageContent> content) {
    this.role = role;
    this.content = content;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public List<ClaudeMessageContent> getContent() {
    return content;
  }

  public void setContent(List<ClaudeMessageContent> content) {
    this.content = content;
  }
}
