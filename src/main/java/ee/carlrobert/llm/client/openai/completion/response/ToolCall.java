package ee.carlrobert.llm.client.openai.completion.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolCall {

  private final String id;
  private final String type;
  private final ToolFunctionResponse function;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ToolCall(
      @JsonProperty("id") String id,
      @JsonProperty("type") String type,
      @JsonProperty("function") ToolFunctionResponse function) {
    this.id = id;
    this.type = type;
    this.function = function;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public ToolFunctionResponse getFunction() {
    return function;
  }
}
