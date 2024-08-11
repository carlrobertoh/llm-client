package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAIChatCompletionRequest implements CompletionRequest {

  private final String model;
  private final List<OpenAIChatCompletionMessage> messages;
  @JsonProperty("max_tokens")
  private final int maxTokens;
  private final double temperature;
  @JsonProperty("frequency_penalty")
  private final double frequencyPenalty;
  @JsonProperty("presence_penalty")
  private final double presencePenalty;
  private final boolean stream;
  @JsonIgnore
  private final String overriddenPath;
  private final List<Tool> tools;
  @JsonProperty("tool_choice")
  private final String toolChoice;
  @JsonProperty("response_format")
  private final ResponseFormat responseFormat;
  private final Boolean webSearchIncluded;
  private final RequestDocumentationDetails documentationDetails;

  private OpenAIChatCompletionRequest(Builder builder) {
    this.model = builder.model;
    this.messages = builder.messages;
    this.maxTokens = builder.maxTokens;
    this.temperature = builder.temperature;
    this.frequencyPenalty = builder.frequencyPenalty;
    this.presencePenalty = builder.presencePenalty;
    this.stream = builder.stream;
    this.overriddenPath = builder.overriddenPath;
    this.tools = builder.tools;
    this.toolChoice = builder.toolChoice;
    this.responseFormat = builder.responseFormat;
    this.webSearchIncluded = builder.webSearchIncluded;
    this.documentationDetails = builder.documentationDetails;
  }

  public void addMessage(OpenAIChatCompletionMessage message) {
    messages.add(message);
  }

  public List<OpenAIChatCompletionMessage> getMessages() {
    return messages;
  }

  public String getModel() {
    return model;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  public double getTemperature() {
    return temperature;
  }

  public double getFrequencyPenalty() {
    return frequencyPenalty;
  }

  public double getPresencePenalty() {
    return presencePenalty;
  }

  public boolean isStream() {
    return stream;
  }

  public String getOverriddenPath() {
    return overriddenPath;
  }

  public List<Tool> getTools() {
    return tools;
  }

  public String getToolChoice() {
    return toolChoice;
  }

  public ResponseFormat getResponseFormat() {
    return responseFormat;
  }

  public Boolean getWebSearchIncluded() {
    return webSearchIncluded;
  }

  public RequestDocumentationDetails getDocumentationDetails() {
    return documentationDetails;
  }

  public static class Builder {

    private final List<OpenAIChatCompletionMessage> messages;
    private String model;
    private int maxTokens = 1000;
    private double temperature = 0.9;
    private double frequencyPenalty = 0;
    private double presencePenalty = 0.6;
    private boolean stream = true;
    private String overriddenPath;
    private List<Tool> tools;
    private String toolChoice;
    private ResponseFormat responseFormat;
    private Boolean webSearchIncluded;
    private RequestDocumentationDetails documentationDetails;

    public Builder(List<OpenAIChatCompletionMessage> messages) {
      this.messages = messages;
    }

    public Builder setModel(OpenAIChatCompletionModel model) {
      this.model = model.getCode();
      return this;
    }

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setMaxTokens(int maxTokens) {
      this.maxTokens = maxTokens;
      return this;
    }

    public Builder setTemperature(double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder setFrequencyPenalty(double frequencyPenalty) {
      this.frequencyPenalty = frequencyPenalty;
      return this;
    }

    public Builder setPresencePenalty(double presencePenalty) {
      this.presencePenalty = presencePenalty;
      return this;
    }

    public Builder setStream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder setOverriddenPath(String overriddenPath) {
      this.overriddenPath = overriddenPath;
      return this;
    }

    public Builder setTools(List<Tool> tools) {
      this.tools = tools;
      return this;
    }

    public Builder setToolChoice(String toolChoice) {
      this.toolChoice = toolChoice;
      return this;
    }

    public Builder setResponseFormat(ResponseFormat responseFormat) {
      this.responseFormat = responseFormat;
      return this;
    }

    public Builder setWebSearchIncluded(Boolean webSearchIncluded) {
      this.webSearchIncluded = webSearchIncluded;
      return this;
    }

    public Builder setDocumentationDetails(RequestDocumentationDetails documentationDetails) {
      this.documentationDetails = documentationDetails;
      return this;
    }

    public OpenAIChatCompletionRequest build() {
      return new OpenAIChatCompletionRequest(this);
    }
  }
}
