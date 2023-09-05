package ee.carlrobert.llm.client.openai.completion.text.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAITextCompletionResponseChoice {

  private final String text;
  private final String finishReason;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public OpenAITextCompletionResponseChoice(
      @JsonProperty("text") String text,
      @JsonProperty("finish_reason") String finishReason) {
    this.text = text;
    this.finishReason = finishReason;
  }

  public String getText() {
    return text;
  }

  public String getFinishReason() {
    return finishReason;
  }
}
