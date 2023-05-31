package ee.carlrobert.openai.client.completion.chat.request;

import ee.carlrobert.openai.client.completion.CompletionRequest;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import java.util.List;

public class ChatCompletionRequest extends CompletionRequest {

  private final String model;
  private final List<ChatCompletionMessage> messages;

  private ChatCompletionRequest(Builder builder) {
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

  public static class Builder extends CompletionRequest.Builder {

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
    public ChatCompletionRequest build() {
      return new ChatCompletionRequest(this);
    }
  }
}
