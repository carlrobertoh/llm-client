package ee.carlrobert.llm.client.openai.completion.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.completion.CompletionResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIChatCompletionResponse implements CompletionResponse {

  private final String id;
  private final List<OpenAIChatCompletionResponseChoice> choices;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public OpenAIChatCompletionResponse(
      @JsonProperty("id") String id,
      @JsonProperty("choices") List<OpenAIChatCompletionResponseChoice> choices) {
    this.id = id;
    this.choices = choices;
  }

  public String getId() {
    return id;
  }

  public List<OpenAIChatCompletionResponseChoice> getChoices() {
    return choices;
  }
}
