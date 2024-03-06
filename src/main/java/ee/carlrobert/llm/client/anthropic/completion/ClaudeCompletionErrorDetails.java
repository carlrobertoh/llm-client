package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeCompletionErrorDetails {

  private String type;
  private ErrorDetails error;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ErrorDetails getError() {
    return error;
  }

  public void setError(ErrorDetails error) {
    this.error = error;
  }
}
