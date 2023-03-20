package ee.carlrobert.openai.client.completion;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompletionRequest {

  @JsonProperty("max_tokens")
  private final int maxTokens;
  private final double temperature;
  @JsonProperty("best_of")
  private final int bestOf;
  @JsonProperty("frequency_penalty")
  private final double frequencyPenalty;
  @JsonProperty("presence_penalty")
  private final double presencePenalty;

  protected CompletionRequest(Builder builder) {
    this.maxTokens = builder.maxTokens;
    this.temperature = builder.temperature;
    this.bestOf = builder.bestOf;
    this.frequencyPenalty = builder.frequencyPenalty;
    this.presencePenalty = builder.presencePenalty;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  public double getTemperature() {
    return temperature;
  }

  public int getBestOf() {
    return bestOf;
  }

  public double getFrequencyPenalty() {
    return frequencyPenalty;
  }

  public double getPresencePenalty() {
    return presencePenalty;
  }

  public boolean isStream() {
    return true;
  }

  protected static class Builder {

    private int maxTokens = 1000;
    private double temperature = 0.9;
    private int bestOf = 1;
    private double frequencyPenalty = 0;
    private double presencePenalty = 0.6;

    public Builder setMaxTokens(int maxTokens) {
      this.maxTokens = maxTokens;
      return this;
    }

    public Builder setTemperature(double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder setBestOf(int bestOf) {
      this.bestOf = bestOf;
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
  }
}
