package ee.carlrobert.llm.client.codegpt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.client.openai.completion.BaseApiResponseError;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeGPTApiResponseError implements BaseApiResponseError {

  private final int status;
  private final String detail;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public CodeGPTApiResponseError(
      @JsonProperty("status") int status,
      @JsonProperty("detail") String detail) {
    this.status = status;
    this.detail = detail;
  }

  public int getStatus() {
    return status;
  }

  public String getDetail() {
    return detail;
  }

  @Override
  public ErrorDetails getError() {
    return new ErrorDetails(detail);
  }
}
