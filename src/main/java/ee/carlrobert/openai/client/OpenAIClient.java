package ee.carlrobert.openai.client;

import ee.carlrobert.openai.client.completion.chat.ChatCompletionClient;
import ee.carlrobert.openai.client.completion.text.TextCompletionClient;
import ee.carlrobert.openai.client.embeddings.EmbeddingsClient;

public class OpenAIClient extends Client {

  private final String organization;

  private OpenAIClient(Builder builder) {
    super(builder);
    this.organization = builder.organization;
  }

  public String getOrganization() {
    return organization;
  }

  public static class Builder extends Client.Builder {

    private String organization;

    public Builder(String apiKey) {
      super(apiKey);
    }

    public Builder setOrganization(String organization) {
      this.organization = organization;
      return this;
    }

    @Override
    public ChatCompletionClient buildChatCompletionClient() {
      return new ChatCompletionClient(new OpenAIClient(this));
    }

    @Override
    public TextCompletionClient buildTextCompletionClient() {
      return new TextCompletionClient(new OpenAIClient(this));
    }

    public EmbeddingsClient buildEmbeddingsClient() {
      return new EmbeddingsClient(new OpenAIClient(this));
    }
  }
}
