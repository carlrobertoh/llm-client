package ee.carlrobert.llm.client.openai.completion.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAITextCompletionResponseChoice {

  private final String text;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public OpenAITextCompletionResponseChoice(@JsonProperty("text") String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
