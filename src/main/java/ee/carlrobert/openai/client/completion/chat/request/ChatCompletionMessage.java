package ee.carlrobert.openai.client.completion.chat.request;

public class ChatCompletionMessage {

  private final String role;
  private final String content;

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
}
