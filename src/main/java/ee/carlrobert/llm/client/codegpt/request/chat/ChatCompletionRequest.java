package ee.carlrobert.llm.client.codegpt.request.chat;

import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;
import java.util.UUID;

public class ChatCompletionRequest implements CompletionRequest {

  private final UUID sessionId;
  private final Double temperature;
  private final Integer maxTokens;
  private final boolean stream;
  private final String model;
  private final List<OpenAIChatCompletionMessage> messages;
  private final boolean webSearchIncluded;
  private final DocumentationDetails documentationDetails;
  private final AdditionalRequestContext context;
  private final Metadata metadata;

  private ChatCompletionRequest(Builder builder) {
    this.model = builder.model;
    this.messages = builder.messages;
    this.maxTokens = builder.maxTokens;
    this.temperature = builder.temperature;
    this.sessionId = builder.sessionId;
    this.stream = builder.stream;
    this.webSearchIncluded = builder.webSearchIncluded;
    this.documentationDetails = builder.documentationDetails;
    this.context = builder.context;
    this.metadata = builder.metadata;
  }

  public Double getTemperature() {
    return temperature;
  }

  public Integer getMaxTokens() {
    return maxTokens;
  }

  public boolean isStream() {
    return stream;
  }

  public String getModel() {
    return model;
  }

  public List<OpenAIChatCompletionMessage> getMessages() {
    return messages;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public boolean isWebSearchIncluded() {
    return webSearchIncluded;
  }

  public DocumentationDetails getDocumentationDetails() {
    return documentationDetails;
  }

  public AdditionalRequestContext getContext() {
    return context;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public static class Builder {

    private Double temperature = 0.9;
    private Integer maxTokens = 4096;
    private boolean stream = true;
    private String model;
    private final List<OpenAIChatCompletionMessage> messages;
    private UUID sessionId;
    private boolean webSearchIncluded;
    private DocumentationDetails documentationDetails;
    private AdditionalRequestContext context;
    private Metadata metadata;

    public Builder(List<OpenAIChatCompletionMessage> messages) {
      this.messages = messages;
    }

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setSessionId(UUID sessionId) {
      this.sessionId = sessionId;
      return this;
    }

    public Builder setMaxTokens(Integer maxTokens) {
      this.maxTokens = maxTokens;
      return this;
    }

    public Builder setTemperature(Double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder setStream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder setWebSearchIncluded(Boolean webSearchIncluded) {
      this.webSearchIncluded = webSearchIncluded;
      return this;
    }

    public Builder setDocumentationDetails(DocumentationDetails documentationDetails) {
      this.documentationDetails = documentationDetails;
      return this;
    }

    public Builder setContext(AdditionalRequestContext context) {
      this.context = context;
      return this;
    }

    public Builder setMetadata(Metadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public ChatCompletionRequest build() {
      return new ChatCompletionRequest(this);
    }
  }
}
