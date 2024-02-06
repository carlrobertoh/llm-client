package ee.carlrobert.llm.client.llama.completion;

import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.ArrayList;
import java.util.List;

public class LlamaCompletionRequest implements CompletionRequest {

  private final String prompt;
  private final int n_predict;
  private final boolean stream;
  private final double temperature;
  private final int top_k;
  private final double top_p;
  private final double min_p;
  private final double repeat_penalty;
  private final List<String> stop;

  public LlamaCompletionRequest(Builder builder) {
    this.prompt = builder.prompt;
    this.stream = builder.stream;
    this.n_predict = builder.n_predict;
    this.temperature = builder.temperature;
    this.top_k = builder.top_k;
    this.top_p = builder.top_p;
    this.min_p = builder.min_p;
    this.repeat_penalty = builder.repeat_penalty;
    this.stop = builder.stop;
  }

  public String getPrompt() {
    return prompt;
  }

  public Boolean isStream() {
    return stream;
  }

  public int getN_predict() {
    return n_predict;
  }

  public double getTemperature() {
    return temperature;
  }

  public int getTop_k() {
    return top_k;
  }

  public double getTop_p() {
    return top_p;
  }

  public double getMin_p() {
    return min_p;
  }

  public double getRepeat_penalty() {
    return repeat_penalty;
  }

  public List<String> getStop() {
    return stop;
  }

  public static class Builder {

    private final String prompt;
    private boolean stream = true;
    private int n_predict = 256;
    private double temperature = 0.1;
    private int top_k = 40;
    private double top_p = 0.9;
    private double min_p = 0.05;
    private double repeat_penalty = 1.1;
    private List<String> stop = new ArrayList<>();

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

    public Builder setTop_k(int top_k) {
      this.top_k = top_k;
      return this;
    }

    public Builder setTop_p(double top_p) {
      this.top_p = top_p;
      return this;
    }

    public Builder setMin_p(double min_p) {
      this.min_p = min_p;
      return this;
    }

    public Builder setRepeat_penalty(double repeat_penalty) {
      this.repeat_penalty = repeat_penalty;
      return this;
    }

    public Builder setStop(List<String> stop) {
      this.stop = stop;
      return this;
    }
    public LlamaCompletionRequest build() {
      return new LlamaCompletionRequest(this);
    }
  }
}
