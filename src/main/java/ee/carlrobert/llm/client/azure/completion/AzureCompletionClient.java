package ee.carlrobert.llm.client.azure.completion;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.azure.AzureClient;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.completion.CompletionClient;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

abstract class AzureCompletionClient extends CompletionClient {

  protected AzureClient client;
  protected String url;

  private static final String BASE_URL = PropertiesLoader.getValue("azure.openai.baseUrl");

  public AzureCompletionClient(AzureClient client, String path) {
    super(client);
    this.client = client;
    this.url = getHost(client) + formatPath(client, path);
  }

  // TODO: Shared logic
  public <R extends CompletionRequest> Request buildHttpRequest(OpenAICompletionRequest completionRequest) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (completionRequest.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(url)
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              new ObjectMapper().writeValueAsString(completionRequest),
              MediaType.parse("application/json")))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getRequiredHeaders() {
    return client.isActiveDirectoryAuthentication() ?
        Map.of("Authorization", "Bearer " + client.getApiKey()) :
        Map.of("api-key", client.getApiKey());
  }

  private static String formatPath(AzureClient client, String path) {
    var params = client.getRequestParams();
    return format(path, params.getDeploymentId(), params.getApiVersion());
  }

  private static String getHost(AzureClient client) {
    return format(client.getHost() == null ? BASE_URL : client.getHost(), client.getRequestParams().getResourceName());
  }
}
