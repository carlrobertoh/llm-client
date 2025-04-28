package ee.carlrobert.llm.client.codegpt.request.chat;

public class ContextFile {

  private final String name;
  private final String path;
  private final String content;

  public ContextFile(String name, String path, String content) {
    this.path = path;
    this.name = name;
    this.content = content;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getContent() {
    return content;
  }
}