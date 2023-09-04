package ee.carlrobert.llm.client.openai.completion.chat.request;

public class ChatCompletionMessage {

  private final String role;
  private String content;

  public ChatCompletionMessage(String role, String content) {
    this.role = role;
    this.content = content;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
