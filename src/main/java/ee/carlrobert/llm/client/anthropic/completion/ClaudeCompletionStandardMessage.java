package ee.carlrobert.llm.client.anthropic.completion;

public class ClaudeCompletionStandardMessage implements ClaudeCompletionMessage {

  private final String role;
  private final String content;

  public ClaudeCompletionStandardMessage(String role, String content) {
    this.role = role;
    this.content = content;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }
}
