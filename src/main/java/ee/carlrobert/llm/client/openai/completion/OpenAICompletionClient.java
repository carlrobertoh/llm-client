package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.completion.CompletionClient;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class OpenAICompletionClient extends CompletionClient {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.baseUrl");

  protected final OpenAIClient client;
  protected final String url;

  public OpenAICompletionClient(OpenAIClient client, String path) {
    super(client);
    this.client = client;
    this.url = (client.getHost() == null ? BASE_URL : client.getHost()) + path;
  }

  protected Request buildHttpRequest(OpenAICompletionRequest completionRequest) {
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
    var headers = new HashMap<>(Map.of(
        "Authorization", "Bearer " + client.getApiKey()
    ));
    var organization = client.getOrganization();
    if (organization != null && !organization.isEmpty()) {
      headers.put("OpenAI-Organization", organization);
    }
    return headers;
  }
}
