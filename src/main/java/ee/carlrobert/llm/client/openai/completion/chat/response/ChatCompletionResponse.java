package ee.carlrobert.llm.client.openai.completion.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponse {

  private final String id;
  private final List<ChatCompletionResponseChoice> choices;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ChatCompletionResponse(
      @JsonProperty("id") String id,
      @JsonProperty("choices") List<ChatCompletionResponseChoice> choices) {
    this.id = id;
    this.choices = choices;
  }

  public String getId() {
    return id;
  }

  public List<ChatCompletionResponseChoice> getChoices() {
    return choices;
  }
}
