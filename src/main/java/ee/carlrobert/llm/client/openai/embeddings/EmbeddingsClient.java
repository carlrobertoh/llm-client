package ee.carlrobert.llm.client.openai.embeddings;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.ClientCode;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.PropertiesLoader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EmbeddingsClient {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.baseUrl");
  private final String baseUrl;
  private final Client client;

  public EmbeddingsClient(OpenAIClient client) {
    this.client = client;
    this.baseUrl = client.getHost() == null ? BASE_URL : client.getHost();
  }

  public ClientCode getClientCode() {
    return ClientCode.EMBEDDINGS;
  }

  public double[] getEmbedding(String input) {
    return getEmbeddings(List.of(input)).get(0);
  }

  public List<double[]> getEmbeddings(List<String> texts) {
    try (var response = client.getHttpClient()
        .newCall(buildRequest(baseUrl + "/v1/embeddings", texts))
        .execute()) {
      if (response.body() != null) {
        return new ObjectMapper()
            .readValue(response.body().string(), EmbeddingResponse.class)
            .getData()
            .stream()
            .map(EmbeddingData::getEmbedding)
            .collect(toList());
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to fetch embedding", e);
    }
    return null;
  }

  private Request buildRequest(String url, List<String> texts) throws JsonProcessingException {
    return new Request.Builder()
        .url(url)
        .headers(Headers.of(Map.of("Authorization", "Bearer " + client.getApiKey())))
        .post(RequestBody.create(
            new ObjectMapper().writeValueAsString(Map.of(
                "input", texts,
                "model", "text-embedding-ada-002")),
            MediaType.parse("application/json")))
        .build();
  }
}
