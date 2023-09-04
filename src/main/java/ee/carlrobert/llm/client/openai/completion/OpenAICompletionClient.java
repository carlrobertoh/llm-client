package ee.carlrobert.llm.client.openai.completion;

import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.PropertiesLoader;
import java.util.HashMap;
import java.util.Map;

public abstract class OpenAICompletionClient extends CompletionClient {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.baseUrl");
  private final OpenAIClient client;

  public OpenAICompletionClient(OpenAIClient client, String path) {
    super(client, client.getHost() == null ? BASE_URL : client.getHost(), path);
    this.client = client;
  }

  @Override
  protected Map<String, String> getRequiredHeaders() {
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
