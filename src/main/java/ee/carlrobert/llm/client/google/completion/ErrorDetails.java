package ee.carlrobert.llm.client.google.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.client.BaseError;
import java.util.StringJoiner;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDetails extends BaseError {

  private final String message;
  private final String status;
  private final String code;

  public ErrorDetails(String message) {
    this(message, null, null);
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ErrorDetails(
      @JsonProperty("message") String message,
      @JsonProperty("status") String status,
      @JsonProperty("code") String code) {
    this.message = message;
    this.status = status;
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public String getStatus() {
    return status;
  }


  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ErrorDetails.class.getSimpleName() + "[", "]")
        .add("message='" + message + "'")
        .add("status='" + status + "'")
        .add("code='" + code + "'")
        .toString();
  }
}
