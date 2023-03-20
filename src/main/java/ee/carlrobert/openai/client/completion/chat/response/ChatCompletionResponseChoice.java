package ee.carlrobert.openai.client.completion.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponseChoice {

  private final ChatCompletionResponseChoiceDelta delta;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ChatCompletionResponseChoice(@JsonProperty("delta") ChatCompletionResponseChoiceDelta delta) {
    this.delta = delta;
  }

  public ChatCompletionResponseChoiceDelta getDelta() {
    return delta;
  }
}
