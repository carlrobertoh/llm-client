package ee.carlrobert.llm.client.ollama.completion.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OllamaCompletionResponse {

  private String model;
  private String createdAt;
  private String response;
  private boolean done;
  private int promptEvalCount;
  private int evalCount;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public int getPromptEvalCount() {
    return promptEvalCount;
  }

  public void setPromptEvalCount(int promptEvalCount) {
    this.promptEvalCount = promptEvalCount;
  }

  public int getEvalCount() {
    return evalCount;
  }

  public void setEvalCount(int evalCount) {
    this.evalCount = evalCount;
  }
}
