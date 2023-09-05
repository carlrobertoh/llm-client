package ee.carlrobert.llm.client.you.completion;

import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;

public class YouCompletionRequest implements CompletionRequest {

  private final String prompt;
  private final List<YouCompletionRequestMessage> messages;

  public YouCompletionRequest(Builder builder) {
    this.prompt = builder.prompt;
    this.messages = builder.messages;
  }

  public String getPrompt() {
    return prompt;
  }

  public List<YouCompletionRequestMessage> getMessages() {
    return messages;
  }

  public static class Builder {

    private final String prompt;
    private List<YouCompletionRequestMessage> messages;

    public Builder(String prompt) {
      this.prompt = prompt;
    }

    public Builder setChatHistory(List<YouCompletionRequestMessage> messages) {
      this.messages = messages;
      return this;
    }

    public YouCompletionRequest build() {
      return new YouCompletionRequest(this);
    }
  }
}
