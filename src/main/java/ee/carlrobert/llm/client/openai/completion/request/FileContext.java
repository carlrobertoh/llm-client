package ee.carlrobert.llm.client.openai.completion.request;

public class FileContext {

  private final String name;
  private final String content;

  public FileContext(String name, String content) {
    this.name = name;
    this.content = content;
  }

  public String getName() {
    return name;
  }

  public String getContent() {
    return content;
  }
}
