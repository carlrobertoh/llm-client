package ee.carlrobert.openai.client.azure;

import static java.lang.String.format;

import ee.carlrobert.openai.PropertiesLoader;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionClient;

abstract class AzureCompletionClient extends CompletionClient {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.azure.baseUrl");

  public AzureCompletionClient(OpenAIClient client, String resourceName, String path) {
    super(client,
        format(client.getHost() == null ? BASE_URL : client.getHost(), resourceName),
        path);
  }

  @Override
  protected ClientCode getClientCode() {
    return ClientCode.AZURE;
  }
}
