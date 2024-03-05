package ee.carlrobert.llm.client.anthropic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionResponse;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionStreamResponse;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
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

  private static final String BASE_URL = PropertiesLoader.getValue("anthropic.baseUrl");

  private final OkHttpClient httpClient;
  private final String apiKey;
  private final String apiVersion;

  public ClaudeClient(String apiKey, String apiVersion, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.apiKey = apiKey;
    this.apiVersion = apiVersion;
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
      throw new RuntimeException(e);
    }
  }

  protected Request buildCompletionRequest(ClaudeCompletionRequest request) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(BASE_URL + "/v1/messages")
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              new ObjectMapper().writeValueAsString(request),
              MediaType.parse("application/json")))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getRequiredHeaders() {
    return new HashMap<>(Map.of("x-api-key", apiKey, "anthropic-version", apiVersion));
  }

  private CompletionEventSourceListener<String> getCompletionEventSourceListener(
      CompletionEventListener<String> eventListener) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          return new ObjectMapper().readValue(data, ClaudeCompletionStreamResponse.class)
              .getDelta()
              .getText();
        } catch (Throwable t) {
          return "";
        }
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        try {
          return new ObjectMapper().readValue(error, ErrorDetails.class);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }
}
