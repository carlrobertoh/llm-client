package ee.carlrobert.llm.client.anthropic;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionEventSourceListener;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionException;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class ClaudeClient {

  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

  private final OkHttpClient httpClient;
  private final String apiKey;
  private final String apiVersion;
  private final String host;

  public ClaudeClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.apiKey = builder.apiKey;
    this.apiVersion = builder.apiVersion;
    this.host = builder.host;
    this.httpClient = httpClientBuilder.build();
  }

  public EventSource getCompletionAsync(
      ClaudeCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient).newEventSource(
        buildCompletionRequest(request),
        getCompletionEventSourceListener(eventListener));
  }

  public ClaudeCompletionResponse getCompletion(ClaudeCompletionRequest request) {
    try (var response = httpClient.newCall(buildCompletionRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, ClaudeCompletionResponse.class);
    } catch (IOException e) {
      throw new ClaudeCompletionException("Failed to communicate with Claude API", e);
    }
  }

  protected Request buildCompletionRequest(ClaudeCompletionRequest request) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(host + "/v1/messages")
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new ClaudeCompletionException("Failed to serialize request to JSON", e);
    }
  }

  private Map<String, String> getRequiredHeaders() {
    return new HashMap<>(Map.of("x-api-key", apiKey, "anthropic-version", apiVersion));
  }

  private CompletionEventSourceListener<String> getCompletionEventSourceListener(
      CompletionEventListener<String> eventListener) {
    return new ClaudeCompletionEventSourceListener(eventListener);
  }

  public static class Builder {

    private final String apiKey;
    private final String apiVersion;
    private String host = PropertiesLoader.getValue("anthropic.baseUrl");

    public Builder(String apiKey, String apiVersion) {
      this.apiKey = apiKey;
      this.apiVersion = apiVersion;
    }

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public ClaudeClient build(OkHttpClient.Builder builder) {
      return new ClaudeClient(this, builder);
    }

    public ClaudeClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
