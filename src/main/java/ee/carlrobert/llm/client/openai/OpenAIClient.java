package ee.carlrobert.llm.client.openai;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.embeddings.EmbeddingData;
import ee.carlrobert.llm.client.openai.embeddings.EmbeddingResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OpenAIClient {

  private final OkHttpClient httpClient;
  private final String apiKey;
  private final String organization;
  private final String host;

  protected OpenAIClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.apiKey = builder.apiKey;
    this.organization = builder.organization;
    this.host = builder.host;
  }

  public EventSource getChatCompletion(
      OpenAICompletionRequest request,
      CompletionEventListener completionEventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildCompletionHttpRequest(request),
            new OpenAIChatCompletionEventSourceListener(completionEventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAICompletionRequest request) {
    try (var response = httpClient.newCall(buildCompletionHttpRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public double[] getEmbedding(String input) {
    return getEmbeddings(List.of(input)).get(0);
  }

  public List<double[]> getEmbeddings(List<String> texts) {
    try (var response = httpClient
        .newCall(buildRequest(host + "/v1/embeddings", texts))
        .execute()) {

      return Optional.ofNullable(DeserializationUtil.mapResponse(response, EmbeddingResponse.class))
          .map(EmbeddingResponse::getData)
          .orElseGet(Collections::emptyList)
          .stream()
          .map(EmbeddingData::getEmbedding)
          .collect(toList());
    } catch (IOException e) {
      throw new RuntimeException("Unable to fetch embedding", e);
    }
  }

  private Request buildRequest(String url, List<String> texts) throws JsonProcessingException {
    return new Request.Builder()
        .url(url)
        .headers(Headers.of(Map.of("Authorization", "Bearer " + apiKey)))
        .post(RequestBody.create(
            new ObjectMapper().writeValueAsString(Map.of(
                "input", texts,
                "model", "text-embedding-ada-002")),
            MediaType.parse("application/json")))
        .build();
  }

  protected Request buildCompletionHttpRequest(OpenAICompletionRequest completionRequest) {
    var headers = new HashMap<>(getRequiredHeaders());
    headers.put("X-LLM-Application-Tag", "codegpt");
    if (completionRequest.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      var overriddenPath = completionRequest.getOverriddenPath();
      return new Request.Builder()
          .url(host + (overriddenPath == null ? "/v1/chat/completions" : overriddenPath))
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
    var headers = new HashMap<>(Map.of("Authorization", "Bearer " + apiKey));
    if (organization != null && !organization.isEmpty()) {
      headers.put("OpenAI-Organization", organization);
    }
    return headers;
  }

  public static class Builder {

    private final String apiKey;
    private String host = PropertiesLoader.getValue("openai.baseUrl");
    private String organization;

    public Builder(String apiKey) {
      this.apiKey = apiKey;
    }

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setOrganization(String organization) {
      this.organization = organization;
      return this;
    }

    public OpenAIClient build(OkHttpClient.Builder builder) {
      return new OpenAIClient(this, builder);
    }

    public OpenAIClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
