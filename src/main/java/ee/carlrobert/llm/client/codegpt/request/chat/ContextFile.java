package ee.carlrobert.llm.client.codegpt.request.chat;

public class ContextFile {

  private final String name;
  private final String content;

  public ContextFile(String name, String content) {
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
