package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeCompletionResponseMessage {

  private String type;
  private String text;
  private String thinking;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getThinking() {
    return thinking;
  }

  public void setThinking(String thinking) {
    this.thinking = thinking;
  }
}
