package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.llm.completion.CompletionResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeCompletionStreamResponse implements CompletionResponse {

  private String type;
  private int index;
  private ClaudeCompletionResponseMessage delta;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public ClaudeCompletionResponseMessage getDelta() {
    return delta;
  }

  public void setDelta(
      ClaudeCompletionResponseMessage delta) {
    this.delta = delta;
  }
}
