package ee.carlrobert.llm.client.openai;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.chat.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.embeddings.EmbeddingData;
import ee.carlrobert.llm.client.openai.embeddings.EmbeddingResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OpenAIClient extends Client {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.baseUrl");

  private final String organization;

  protected OpenAIClient(Builder builder) {
    super(builder);
    this.organization = builder.organization;
  }

  public EventSource getChatCompletion(OpenAICompletionRequest request, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(getHttpClient())
        .newEventSource(buildCompletionHttpRequest(request), new OpenAIChatCompletionEventSourceListener(completionEventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAICompletionRequest request) {
    try (var response = getHttpClient().newCall(buildCompletionHttpRequest(request)).execute()) {
      return new ObjectMapper().readValue(Objects.requireNonNull(response.body()).string(), OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public double[] getEmbedding(String input) {
    return getEmbeddings(List.of(input)).get(0);
  }

  public List<double[]> getEmbeddings(List<String> texts) {
    try (var response = getHttpClient()
        .newCall(buildRequest((getHost() == null ? BASE_URL : getHost()) + "/v1/embeddings", texts))
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
        .headers(Headers.of(Map.of("Authorization", "Bearer " + getApiKey())))
        .post(RequestBody.create(
            new ObjectMapper().writeValueAsString(Map.of(
                "input", texts,
                "model", "text-embedding-ada-002")),
            MediaType.parse("application/json")))
        .build();
  }

  protected Request buildCompletionHttpRequest(OpenAICompletionRequest completionRequest) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (completionRequest.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      var host = getHost();
      var overriddenPath = completionRequest.getOverriddenPath();
      return new Request.Builder()
          .url((host == null ? BASE_URL : host) + (overriddenPath == null ? "/v1/chat/completions" : overriddenPath))
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
        "Authorization", "Bearer " + getApiKey()
    ));
    if (organization != null && !organization.isEmpty()) {
      headers.put("OpenAI-Organization", organization);
    }
    return headers;
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

    public OpenAIClient build() {
      return new OpenAIClient(this);
    }
  }
}
