package ee.carlrobert.llm.client.inception.request;

import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;

public class InceptionNextEditRequest implements CompletionRequest {

  private final String model;
  private final List<OpenAIChatCompletionMessage> messages;

  private InceptionNextEditRequest(Builder builder) {
    this.model = builder.model;
    this.messages = builder.messages;
  }

  public String getModel() {
    return model;
  }

  public List<OpenAIChatCompletionMessage> getMessages() {
    return messages;
  }

  public static class Builder {
    private String model;
    private List<OpenAIChatCompletionMessage> messages;

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setMessages(List<OpenAIChatCompletionMessage> messages) {
      this.messages = messages;
      return this;
    }

    public InceptionNextEditRequest build() {
      return new InceptionNextEditRequest(this);
    }
  }
}

