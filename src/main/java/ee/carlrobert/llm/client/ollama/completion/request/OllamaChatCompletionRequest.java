package ee.carlrobert.llm.client.ollama.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaResponseFormat;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;

/*
 * See <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion">ollama/api</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OllamaChatCompletionRequest implements CompletionRequest {

  private final String model;
  private final List<OllamaChatCompletionMessage> messages;
  private final OllamaResponseFormat format;
  private final OllamaParameters options;
  private final Boolean stream;
  private final String keepAlive;

  public OllamaChatCompletionRequest(Builder builder) {
    this.model = builder.model;
    this.messages = builder.messages;
    this.format = builder.format;
    this.options = builder.options;
    this.stream = builder.stream;
    this.keepAlive = builder.keepAlive;
  }

  public String getModel() {
    return model;
  }

  public List<OllamaChatCompletionMessage> getMessages() {
    return messages;
  }

  public OllamaResponseFormat getFormat() {
    return format;
  }

  public OllamaParameters getOptions() {
    return options;
  }

  public Boolean getStream() {
    return stream;
  }

  public String getKeepAlive() {
    return keepAlive;
  }

  public static class Builder {

    private final String model;
    private final List<OllamaChatCompletionMessage> messages;

    private OllamaResponseFormat format = null;
    private OllamaParameters options = null;
    private Boolean stream = null;
    private String keepAlive = null;

    public Builder(String model, List<OllamaChatCompletionMessage> messages) {
      this.model = model;
      this.messages = messages;
    }

    public Builder setFormat(OllamaResponseFormat format) {
      this.format = format;
      return this;
    }

    public Builder setOptions(OllamaParameters options) {
      this.options = options;
      return this;
    }

    public Builder setStream(Boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder setKeepAlive(String keepAlive) {
      this.keepAlive = keepAlive;
      return this;
    }

    public OllamaChatCompletionRequest build() {
      return new OllamaChatCompletionRequest(this);
    }
  }
}