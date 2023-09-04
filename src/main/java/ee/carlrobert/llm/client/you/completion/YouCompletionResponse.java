package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouCompletionResponse {

  private final String youChatToken;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public YouCompletionResponse(@JsonProperty("youChatToken") String youChatToken) {
    this.youChatToken = youChatToken;
  }

  public String getYouChatToken() {
    return youChatToken;
  }
}
