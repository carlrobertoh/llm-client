package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;

public class ClaudeCompletionRequest implements CompletionRequest {

  private String model;
  private String system;
  private List<ClaudeCompletionMessage> messages;
  @JsonProperty("max_tokens")
  private int maxTokens;
  private boolean stream;
  private double temperature;
  @JsonProperty("top_k")
  private int topK;
  @JsonProperty("top_p")
  private int topP;
  @JsonProperty("stop_sequences")
  private List<String> stopSequences;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public List<ClaudeCompletionMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ClaudeCompletionMessage> messages) {
    this.messages = messages;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  public void setMaxTokens(int maxTokens) {
    this.maxTokens = maxTokens;
  }

  public boolean isStream() {
    return stream;
  }

  public void setStream(boolean stream) {
    this.stream = stream;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public int getTopK() {
    return topK;
  }

  public void setTopK(int topK) {
    this.topK = topK;
  }

  public int getTopP() {
    return topP;
  }

  public void setTopP(int topP) {
    this.topP = topP;
  }

  public List<String> getStopSequences() {
    return stopSequences;
  }

  public void setStopSequences(List<String> stopSequences) {
    this.stopSequences = stopSequences;
  }
}
