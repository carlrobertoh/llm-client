package ee.carlrobert.llm.client.codegpt.request;

public class AutoApplyRequest {

  private String suggestedChanges;
  private String fileContent;

  public String getSuggestedChanges() {
    return suggestedChanges;
  }

  public void setSuggestedChanges(String suggestedChanges) {
    this.suggestedChanges = suggestedChanges;
  }

  public String getFileContent() {
    return fileContent;
  }

  public void setFileContent(String fileContent) {
    this.fileContent = fileContent;
  }
}
