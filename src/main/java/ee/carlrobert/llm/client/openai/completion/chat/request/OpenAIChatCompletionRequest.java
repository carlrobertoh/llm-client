package ee.carlrobert.llm.client.openai.completion.chat.request;

import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import java.util.List;

public class OpenAIChatCompletionRequest extends OpenAICompletionRequest {

  private final String model;
  private final List<OpenAIChatCompletionMessage> messages;

  private OpenAIChatCompletionRequest(Builder builder) {
    super(builder);
    this.model = builder.model;
    this.messages = builder.messages;
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

  public static class Builder extends OpenAICompletionRequest.Builder {

    private final List<OpenAIChatCompletionMessage> messages;
    private String model = OpenAIChatCompletionModel.GPT_3_5.getCode();

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

    @Override
    public OpenAIChatCompletionRequest build() {
      return new OpenAIChatCompletionRequest(this);
    }
  }
}
