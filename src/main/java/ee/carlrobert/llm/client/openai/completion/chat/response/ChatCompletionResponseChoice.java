package ee.carlrobert.llm.client.openai.completion.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponseChoice {

  private final ChatCompletionResponseChoiceDelta delta;
  private final ChatCompletionResponseChoiceDelta message;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ChatCompletionResponseChoice(
      @JsonProperty("delta") ChatCompletionResponseChoiceDelta delta,
      @JsonProperty("message") ChatCompletionResponseChoiceDelta message) {
    this.delta = delta;
    this.message = message;
  }

  public ChatCompletionResponseChoiceDelta getDelta() {
    return delta;
  }

  public ChatCompletionResponseChoiceDelta getMessage() {
    return message;
  }
}
