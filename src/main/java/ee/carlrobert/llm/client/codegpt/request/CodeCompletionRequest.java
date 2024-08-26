package ee.carlrobert.llm.client.codegpt.request;

import ee.carlrobert.llm.completion.CompletionRequest;

public class CodeCompletionRequest implements CompletionRequest {

  private final String model;
  private final String prefix;
  private final String suffix;
  private final String fileExtension;
  private final String fileContent;
  private final String stagedDiff;
  private final String unstagedDiff;

  protected CodeCompletionRequest(CodeCompletionRequest.Builder builder) {
    this.model = builder.model;
    this.prefix = builder.prefix;
    this.suffix = builder.suffix;
    this.fileExtension = builder.fileExtension;
    this.fileContent = builder.fileContent;
    this.stagedDiff = builder.stagedDiff;
    this.unstagedDiff = builder.unstagedDiff;
  }

  public String getModel() {
    return model;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public String getFileContent() {
    return fileContent;
  }

  public String getStagedDiff() {
    return stagedDiff;
  }

  public String getUnstagedDiff() {
    return unstagedDiff;
  }

  public static class Builder {

    private String model = "gpt-3.5-turbo-instruct";
    private String prefix;
    private String suffix;
    private String fileExtension;
    private String fileContent;
    private String stagedDiff;
    private String unstagedDiff;

    public CodeCompletionRequest.Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public CodeCompletionRequest.Builder setPrefix(String prefix) {
      this.prefix = prefix;
      return this;
    }


    public CodeCompletionRequest.Builder setSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    public CodeCompletionRequest.Builder setFileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
      return this;
    }

    public CodeCompletionRequest.Builder setFileContent(String fileContent) {
      this.fileContent = fileContent;
      return this;
    }

    public CodeCompletionRequest.Builder setStagedDiff(String stagedDiff) {
      this.stagedDiff = stagedDiff;
      return this;
    }

    public CodeCompletionRequest.Builder setUnstagedDiff(String unstagedDiff) {
      this.unstagedDiff = unstagedDiff;
      return this;
    }

    public CodeCompletionRequest build() {
      return new CodeCompletionRequest(this);
    }
  }
}