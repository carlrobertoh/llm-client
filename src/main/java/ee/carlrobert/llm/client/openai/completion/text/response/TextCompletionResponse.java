package ee.carlrobert.llm.client.openai.completion.text.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextCompletionResponse {

  private final String id;
  private final List<TextCompletionResponseChoice> choices;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public TextCompletionResponse(
      @JsonProperty("id") String id,
      @JsonProperty("choices") List<TextCompletionResponseChoice> choices) {
    this.id = id;
    this.choices = choices;
  }

  public String getId() {
    return id;
  }

  public List<TextCompletionResponseChoice> getChoices() {
    return choices;
  }
}
