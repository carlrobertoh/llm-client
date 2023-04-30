package ee.carlrobert.openai.client.azure;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.openai.client.completion.BaseApiResponseError;
import ee.carlrobert.openai.client.completion.ErrorDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureApiResponseError implements BaseApiResponseError {

  private final int statusCode;
  private final String message;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public AzureApiResponseError(
      @JsonProperty("error") ErrorDetails error,
      @JsonProperty("statusCode") int statusCode,
      @JsonProperty("message") String message) {
    if (error != null) {
      this.statusCode = Integer.getInteger(error.getCode(), 0);
      this.message = error.getMessage();
    } else {
      this.statusCode = statusCode;
      this.message = message;
    }
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public ErrorDetails getError() {
    return new ErrorDetails(message);
  }
}
