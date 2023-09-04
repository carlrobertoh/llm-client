package ee.carlrobert.llm.client.openai.completion.chat.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponseChoiceDelta {

  private final String role;
  private final String content;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ChatCompletionResponseChoiceDelta(
      @JsonProperty("role") String role,
      @JsonProperty("content") String content) {
    this.role = role;
    this.content = content;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }
}
