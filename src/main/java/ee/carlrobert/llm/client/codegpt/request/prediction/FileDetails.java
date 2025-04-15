package ee.carlrobert.llm.client.codegpt.request.prediction;

public class FileDetails {

  private String name;
  private String content;
  private Long modificationStamp;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getModificationStamp() {
    return modificationStamp;
  }

  public void setModificationStamp(Long modificationStamp) {
    this.modificationStamp = modificationStamp;
  }
}