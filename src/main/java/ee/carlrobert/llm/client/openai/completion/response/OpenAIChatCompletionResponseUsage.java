package ee.carlrobert.llm.client.openai.completion.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIChatCompletionResponseUsage {

  private final int promptTokens;
  private final int completionTokens;
  private final int totalTokens;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public OpenAIChatCompletionResponseUsage(
      @JsonProperty("prompt_tokens") int promptTokens,
      @JsonProperty("completion_tokens") int completionTokens,
      @JsonProperty("total_tokens") int totalTokens) {
    this.promptTokens = promptTokens;
    this.completionTokens = completionTokens;
    this.totalTokens = totalTokens;
  }

  public int getPromptTokens() {
    return promptTokens;
  }

  public int getCompletionTokens() {
    return completionTokens;
  }

  public int getTotalTokens() {
    return totalTokens;
  }
}