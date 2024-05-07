package ee.carlrobert.llm.client.google.completion;

import java.util.List;

/**
 * <a href="https://ai.google.dev/api/rest/v1/GenerationConfig?authuser=1">Gemini API
 * GenerationConfig</a>.
 */
public class GoogleGenerationConfig {

  private final List<String> stopSequences;
  private final int candidateCount;
  private final double temperature;
  private final int maxOutputTokens;
  private final double topP;
  private final int topK;

  public GoogleGenerationConfig(Builder builder) {
    this.stopSequences = builder.stopSequences;
    this.candidateCount = builder.candidateCount;
    this.temperature = builder.temperature;
    this.maxOutputTokens = builder.maxOutputTokens;
    this.topP = builder.topP;
    this.topK = builder.topK;
  }

  public List<String> getStopSequences() {
    return stopSequences;
  }

  public int getCandidateCount() {
    return candidateCount;
  }

  public double getTemperature() {
    return temperature;
  }

  public int getMaxOutputTokens() {
    return maxOutputTokens;
  }

  public double getTopP() {
    return topP;
  }

  public int getTopK() {
    return topK;
  }

  public static class Builder {

    private List<String> stopSequences;
    private int candidateCount = 1;
    private double temperature = 0.9;
    private int maxOutputTokens = 256;
    private double topP = 0.9;
    private int topK = 40;

    public Builder stopSequences(List<String> stopSequences) {
      this.stopSequences = stopSequences;
      return this;
    }

    public Builder candidateCount(int candidateCount) {
      this.candidateCount = candidateCount;
      return this;
    }

    public Builder temperature(double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder maxOutputTokens(int maxOutputTokens) {
      this.maxOutputTokens = maxOutputTokens;
      return this;
    }

    public Builder topP(double topP) {
      this.topP = topP;
      return this;
    }

    public Builder topK(int topK) {
      this.topK = topK;
      return this;
    }

    public GoogleGenerationConfig build() {
      return new GoogleGenerationConfig(this);
    }
  }
}
