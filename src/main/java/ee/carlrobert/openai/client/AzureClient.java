package ee.carlrobert.openai.client;

import ee.carlrobert.openai.client.azure.AzureChatCompletionClient;
import ee.carlrobert.openai.client.azure.AzureClientRequestParams;
import ee.carlrobert.openai.client.azure.AzureTextCompletionClient;

public class AzureClient extends Client {

  private final AzureClientRequestParams requestParams;
  private final boolean activeDirectoryAuthentication;

  private AzureClient(Builder builder) {
    super(builder);
    this.requestParams = builder.requestParams;
    this.activeDirectoryAuthentication = builder.activeDirectoryAuthentication;
  }

  public boolean isActiveDirectoryAuthentication() {
    return activeDirectoryAuthentication;
  }

  public AzureClientRequestParams getRequestParams() {
    return requestParams;
  }

  public static class Builder extends Client.Builder {

    private final AzureClientRequestParams requestParams;
    private boolean activeDirectoryAuthentication;

    public Builder(String apiKey, AzureClientRequestParams requestParams) {
      super(apiKey);
      this.requestParams = requestParams;
    }

    public Builder setActiveDirectoryAuthentication(boolean activeDirectoryAuthentication) {
      this.activeDirectoryAuthentication = activeDirectoryAuthentication;
      return this;
    }

    @Override
    public AzureChatCompletionClient buildChatCompletionClient() {
      return new AzureChatCompletionClient(new AzureClient(this));
    }

    @Override
    public AzureTextCompletionClient buildTextCompletionClient() {
      return new AzureTextCompletionClient(new AzureClient(this));
    }
  }
}
