package ee.carlrobert.openai.client.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseError {

  private final ErrorDetails error;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponseError(@JsonProperty("error") ErrorDetails error) {
    this.error = error;
  }

  public ErrorDetails getError() {
    return error;
  }
}
