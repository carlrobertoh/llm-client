package ee.carlrobert.llm.client.llama.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.llm.completion.CompletionResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LlamaCompletionResponse implements CompletionResponse {

  private String content;
  private boolean stop;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isStop() {
    return stop;
  }

  public void setStop(boolean stop) {
    this.stop = stop;
  }
}
