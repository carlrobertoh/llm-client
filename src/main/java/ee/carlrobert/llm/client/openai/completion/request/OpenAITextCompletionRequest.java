package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.completion.CompletionRequest;

public class OpenAITextCompletionRequest implements CompletionRequest {

  private final String model;
  private final String prompt;
  private final String suffix;
  @JsonProperty("max_tokens")
  private final int maxTokens;
  private final double temperature;
  @JsonProperty("frequency_penalty")
  private final double frequencyPenalty;
  @JsonProperty("presence_penalty")
  private final double presencePenalty;
  private final boolean stream;

  protected OpenAITextCompletionRequest(Builder builder) {
    this.model = builder.model;
    this.prompt = builder.prompt;
    this.suffix = builder.suffix;
    this.maxTokens = builder.maxTokens;
    this.temperature = builder.temperature;
    this.frequencyPenalty = builder.frequencyPenalty;
    this.presencePenalty = builder.presencePenalty;
    this.stream = builder.stream;
  }

  public int getMaxTokens() {
    return maxTokens;
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

  public double getTemperature() {
    return temperature;
  }

  public double getFrequencyPenalty() {
    return frequencyPenalty;
  }

  public double getPresencePenalty() {
    return presencePenalty;
  }

  public boolean isStream() {
    return stream;
  }

  public static class Builder {

    private final String prompt;

    private String model = "gpt-3.5-turbo-instruct";
    private String suffix;
    private int maxTokens = 1000;
    private double temperature = 0.9;
    private double frequencyPenalty = 0;
    private double presencePenalty = 0.6;
    private boolean stream = true;

    public Builder(String prompt) {
      this.prompt = prompt;
    }

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    public Builder setMaxTokens(int maxTokens) {
      this.maxTokens = maxTokens;
      return this;
    }

    public Builder setTemperature(double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder setFrequencyPenalty(double frequencyPenalty) {
      this.frequencyPenalty = frequencyPenalty;
      return this;
    }

    public Builder setPresencePenalty(double presencePenalty) {
      this.presencePenalty = presencePenalty;
      return this;
    }

    public Builder setStream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public OpenAITextCompletionRequest build() {
      return new OpenAITextCompletionRequest(this);
    }
  }
}
