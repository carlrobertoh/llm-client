package ee.carlrobert.llm.client.azure;

import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.azure.completion.AzureChatCompletionClient;
import ee.carlrobert.llm.client.azure.completion.AzureCompletionRequestParams;
import ee.carlrobert.llm.client.azure.completion.AzureTextCompletionClient;

public class AzureClient extends Client {

  private final AzureCompletionRequestParams requestParams;
  private final boolean activeDirectoryAuthentication;

  private AzureClient(Builder builder) {
    super(builder);
    this.requestParams = builder.requestParams;
    this.activeDirectoryAuthentication = builder.activeDirectoryAuthentication;
  }

  public boolean isActiveDirectoryAuthentication() {
    return activeDirectoryAuthentication;
  }

  public AzureCompletionRequestParams getRequestParams() {
    return requestParams;
  }

  public static class Builder extends Client.Builder {

    private final AzureCompletionRequestParams requestParams;
    private boolean activeDirectoryAuthentication;

    public Builder(String apiKey, AzureCompletionRequestParams requestParams) {
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
