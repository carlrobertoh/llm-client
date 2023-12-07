package ee.carlrobert.llm.client.openai.completion.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIChatCompletionResponseChoice {

  private final OpenAIChatCompletionResponseChoiceDelta delta;
  private final OpenAIChatCompletionResponseChoiceDelta message;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public OpenAIChatCompletionResponseChoice(
      @JsonProperty("delta") OpenAIChatCompletionResponseChoiceDelta delta,
      @JsonProperty("message") OpenAIChatCompletionResponseChoiceDelta message) {
    this.delta = delta;
    this.message = message;
  }

  public OpenAIChatCompletionResponseChoiceDelta getDelta() {
    return delta;
  }

  public OpenAIChatCompletionResponseChoiceDelta getMessage() {
    return message;
  }
}
