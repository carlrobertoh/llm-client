package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeCompletionResponseMessage {

  private String type;
  private String text;
  private String thinking;
  private String id;
  private String name;
  private Map<String, Object> input;
  @JsonProperty("partial_json")
  private String partialJson;

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Object> getInput() {
    return input;
  }

  public void setInput(Map<String, Object> input) {
    this.input = input;
  }

  public String getPartialJson() {
    return partialJson;
  }

  public void setPartialJson(String partialJson) {
    this.partialJson = partialJson;
  }

  public boolean isToolUse() {
    return "tool_use".equals(type);
  }
  
  public boolean isTextDelta() {
    return "text_delta".equals(type);
  }
}
