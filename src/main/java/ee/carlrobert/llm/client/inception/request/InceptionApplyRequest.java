package ee.carlrobert.llm.client.inception.request;

import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;

public class InceptionApplyRequest implements CompletionRequest {

  private final String model;
  private final List<OpenAIChatCompletionMessage> messages;
  private final boolean stream;
  private final boolean diffusing;

  private InceptionApplyRequest(Builder builder) {
    this.model = builder.model;
    this.messages = builder.messages;
    this.stream = builder.stream;
    this.diffusing = builder.diffusing;
  }

  public String getModel() {
    return model;
  }

  public List<OpenAIChatCompletionMessage> getMessages() {
    return messages;
  }

  public boolean isStream() {
    return stream;
  }

  public boolean isDiffusing() {
    return diffusing;
  }

  public static class Builder {
    private String model;
    private List<OpenAIChatCompletionMessage> messages;
    private boolean stream;
    private boolean diffusing;

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setMessages(List<OpenAIChatCompletionMessage> messages) {
      this.messages = messages;
      return this;
    }

    public Builder setStream(boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder setDiffusing(boolean diffusing) {
      this.diffusing = diffusing;
      return this;
    }

    public InceptionApplyRequest build() {
      return new InceptionApplyRequest(this);
    }
  }
}

