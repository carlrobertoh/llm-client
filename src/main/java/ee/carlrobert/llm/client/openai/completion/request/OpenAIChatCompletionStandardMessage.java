package ee.carlrobert.llm.client.openai.completion.request;

public class OpenAIChatCompletionStandardMessage implements OpenAIChatCompletionMessage {

  private final String role;
  private final String content;

  public OpenAIChatCompletionStandardMessage(String role, String content) {
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
