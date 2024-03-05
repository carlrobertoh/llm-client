package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeCompletionResponseUsage {

  @JsonProperty("input_tokens")
  private int inputTokens;
  @JsonProperty("output_tokens")
  private int outputTokens;

  public int getInputTokens() {
    return inputTokens;
  }

  public void setInputTokens(int inputTokens) {
    this.inputTokens = inputTokens;
  }

  public int getOutputTokens() {
    return outputTokens;
  }

  public void setOutputTokens(int outputTokens) {
    this.outputTokens = outputTokens;
  }
}
