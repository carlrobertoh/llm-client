package ee.carlrobert.llm.client.azure.completion;

import static java.lang.String.format;

import ee.carlrobert.llm.client.azure.AzureClient;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.openai.completion.CompletionClient;
import java.util.Map;

abstract class AzureCompletionClient extends CompletionClient {

  private final AzureClient client;

  private static final String BASE_URL = PropertiesLoader.getValue("azure.openai.baseUrl");

  public AzureCompletionClient(AzureClient client, String path) {
    super(client, getHost(client), formatPath(client, path));
    this.client = client;
  }

  private static String formatPath(AzureClient client, String path) {
    var params = client.getRequestParams();
    return format(path, params.getDeploymentId(), params.getApiVersion());
  }

  private static String getHost(AzureClient client) {
    return format(
        client.getHost() == null ? BASE_URL : client.getHost(),
        client.getRequestParams().getResourceName());
  }

  @Override
  protected Map<String, String> getRequiredHeaders() {
    return client.isActiveDirectoryAuthentication() ?
        Map.of("Authorization", "Bearer " + client.getApiKey()) :
        Map.of("api-key", client.getApiKey());
  }
}
