package ee.carlrobert.llm.client.llama.completion;

import ee.carlrobert.llm.completion.CompletionRequest;

public class LlamaCompletionRequest implements CompletionRequest {

  private final String prompt;
  private final int n_predict;

  public LlamaCompletionRequest(Builder builder) {
    this.prompt = builder.prompt;
    this.n_predict = builder.n_predict;
  }

  public String getPrompt() {
    return prompt;
  }

  public int getN_predict() {
    return n_predict;
  }

  public boolean getStream() {
    return true;
  }

  public static class Builder {

    private final String prompt;
    private int n_predict;

    public Builder(String prompt) {
      this.prompt = prompt;
    }

    public Builder setN_predict(int n_predict) {
      this.n_predict = n_predict;
      return this;
    }

    public LlamaCompletionRequest build() {
      return new LlamaCompletionRequest(this);
    }
  }
}
