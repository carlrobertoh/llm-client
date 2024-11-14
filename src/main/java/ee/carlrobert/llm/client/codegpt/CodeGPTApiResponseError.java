package ee.carlrobert.llm.client.codegpt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.client.openai.completion.BaseApiResponseError;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeGPTApiResponseError implements BaseApiResponseError {

  private final int status;
  private final String detail;
  private final String type;
  private final String code;

  @JsonCreator(mode = Mode.PROPERTIES)
  public CodeGPTApiResponseError(
      @JsonProperty("status") int status,
      @JsonProperty("detail") String detail,
      @JsonProperty("type") String type,
      @JsonProperty("code") String code) {
    this.status = status;
    this.detail = detail;
    this.type = type;
    this.code = code;
  }

  public int getStatus() {
    return status;
  }

  public String getDetail() {
    return detail;
  }

  public String getType() {
    return type;
  }

  public String getCode() {
    return code;
  }

  @Override
  public ErrorDetails getError() {
    return new ErrorDetails(detail, type, null, code);
  }
}
