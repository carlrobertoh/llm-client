package ee.carlrobert.llm.client.openai.completion.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.completion.CompletionResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAITextCompletionResponse implements CompletionResponse {

  private final String id;
  private final List<OpenAITextCompletionResponseChoice> choices;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public OpenAITextCompletionResponse(
      @JsonProperty("id") String id,
      @JsonProperty("choices") List<OpenAITextCompletionResponseChoice> choices) {
    this.id = id;
    this.choices = choices;
  }

  public String getId() {
    return id;
  }

  public List<OpenAITextCompletionResponseChoice> getChoices() {
    return choices;
  }
}