package ee.carlrobert.llm.client.llama.completion;

import ee.carlrobert.llm.completion.CompletionRequest;

public class LlamaCompletionRequest implements CompletionRequest {

  private final String prompt;
  private final int n_predict;
  private final boolean stream;
  private final double temperature;

  public LlamaCompletionRequest(Builder builder) {
    this.prompt = builder.prompt;
    this.stream = builder.stream;
    this.n_predict = builder.n_predict;
    this.temperature = builder.temperature;
  }

  public String getPrompt() {
    return prompt;
  }

  public boolean isStream() {
    return stream;
  }

  public int getN_predict() {
    return n_predict;
  }

  public double getTemperature() {
    return temperature;
  }

  public static class Builder {

    private final String prompt;
    private boolean stream = true;
    private int n_predict = 256;
    private double temperature = 0.1;

    public Builder(String prompt) {
      this.prompt = prompt;
    }

    public Builder setStream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder setN_predict(int n_predict) {
      this.n_predict = n_predict;
      return this;
    }

    public Builder setTemperature(double temperature) {
      this.temperature = temperature;
      return this;
    }

    public LlamaCompletionRequest build() {
      return new LlamaCompletionRequest(this);
    }
  }
}
