package ee.carlrobert.llm.client.openai.completion.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolFunctionResponse {

  private final String name;
  private final String arguments;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ToolFunctionResponse(
      @JsonProperty("name") String name,
      @JsonProperty("arguments") String arguments) {
    this.name = name;
    this.arguments = arguments;
  }

  public String getName() {
    return name;
  }

  public String getArguments() {
    return arguments;
  }
}
