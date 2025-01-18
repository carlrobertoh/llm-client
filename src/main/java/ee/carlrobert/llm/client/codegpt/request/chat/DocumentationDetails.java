package ee.carlrobert.llm.client.codegpt.request.chat;

public class DocumentationDetails {

  private final String name;
  private final String url;

  public DocumentationDetails(String name, String url) {
    this.name = name;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }
}
