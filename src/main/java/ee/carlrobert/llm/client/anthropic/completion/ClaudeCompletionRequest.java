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
  private Double temperature = 0.0;
  @JsonProperty("top_k")
  private Integer topK;
  @JsonProperty("top_p")
  private Integer topP;
  @JsonProperty("stop_sequences")
  private List<String> stopSequences;
  private ClaudeCompletionRequestThinking thinking;
  private List<ClaudeTool> tools;
  @JsonProperty("tool_choice")
  private ClaudeToolChoice toolChoice;

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

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public Integer getTopK() {
    return topK;
  }

  public void setTopK(Integer topK) {
    this.topK = topK;
  }

  public Integer getTopP() {
    return topP;
  }

  public void setTopP(Integer topP) {
    this.topP = topP;
  }

  public List<String> getStopSequences() {
    return stopSequences;
  }

  public void setStopSequences(List<String> stopSequences) {
    this.stopSequences = stopSequences;
  }

  public ClaudeCompletionRequestThinking getThinking() {
    return thinking;
  }

  public void setThinking(ClaudeCompletionRequestThinking thinking) {
    this.thinking = thinking;
  }

  public List<ClaudeTool> getTools() {
    return tools;
  }

  public void setTools(List<ClaudeTool> tools) {
    this.tools = tools;
  }

  public ClaudeToolChoice getToolChoice() {
    return toolChoice;
  }

  public void setToolChoice(ClaudeToolChoice toolChoice) {
    this.toolChoice = toolChoice;
  }
}
