package ee.carlrobert.llm.client.inception.request;

import ee.carlrobert.llm.completion.CompletionRequest;

public class InceptionFIMRequest implements CompletionRequest {

  private final String model;
  private final String prompt;
  private final String suffix;
  private final boolean stream;

  private InceptionFIMRequest(Builder builder) {
    this.model = builder.model;
    this.prompt = builder.prompt;
    this.suffix = builder.suffix;
    this.stream = builder.stream;
  }

  public String getModel() {
    return model;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getSuffix() {
    return suffix;
  }

  public boolean isStream() {
    return stream;
  }

  public static class Builder {
    private String model;
    private String prompt;
    private String suffix;
    private boolean stream = true;

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setPrompt(String prompt) {
      this.prompt = prompt;
      return this;
    }

    public Builder setSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    public Builder setStream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public InceptionFIMRequest build() {
      return new InceptionFIMRequest(this);
    }
  }
}
