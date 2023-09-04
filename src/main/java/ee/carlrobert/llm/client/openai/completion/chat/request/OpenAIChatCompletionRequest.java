package ee.carlrobert.llm.client.openai.completion.chat.request;

import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.client.openai.completion.chat.ChatCompletionModel;
import java.util.List;

public class OpenAIChatCompletionRequest extends OpenAICompletionRequest {

  private final String model;
  private final List<ChatCompletionMessage> messages;

  private OpenAIChatCompletionRequest(Builder builder) {
    super(builder);
    this.model = builder.model;
    this.messages = builder.messages;
  }

  public void addMessage(ChatCompletionMessage message) {
    messages.add(message);
  }

  public List<ChatCompletionMessage> getMessages() {
    return messages;
  }

  public String getModel() {
    return model;
  }

  public static class Builder extends OpenAICompletionRequest.Builder {

    private final List<ChatCompletionMessage> messages;
    private String model = ChatCompletionModel.GPT_3_5.getCode();

    public Builder(List<ChatCompletionMessage> messages) {
      this.messages = messages;
    }

    public Builder setModel(ChatCompletionModel model) {
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
