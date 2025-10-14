package ee.carlrobert.llm.client.openai.response.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponseStreamEvent {

  private String type;
  @JsonProperty("sequence_number")
  private Integer sequenceNumber;
  private OpenAIResponseCompletionResponse response;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public OpenAIResponseCompletionResponse getResponse() {
    return response;
  }

  public void setResponse(OpenAIResponseCompletionResponse response) {
    this.response = response;
  }
}

