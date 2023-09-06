package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.completion.CompletionResponse;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouCompletionResponse implements CompletionResponse {

  private final String chatToken;
  private final List<YouSerpResult> serpResults;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public YouCompletionResponse(
      @JsonProperty("youChatToken") String chatToken,
      @JsonProperty("youChatSerpResults") List<YouSerpResult> serpResults) {
    this.chatToken = chatToken;
    this.serpResults = serpResults;
  }

  public String getChatToken() {
    return chatToken;
  }

  public List<YouSerpResult> getSerpResults() {
    return serpResults;
  }
}
