package ee.carlrobert.llm.client.ollama;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.ollama.completion.OllamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.OllamaCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OllamaClient {

  private static final String BASE_URL = PropertiesLoader.getValue("ollama.baseUrl");

  private final OkHttpClient httpClient;
  private final String host;
  private final Integer port;

  protected OllamaClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.host = builder.host;
    this.port = builder.port;
  }

  public EventSource getChatCompletionAsync(
      OllamaCompletionRequest request,
      CompletionEventListener eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(buildCompletionHttpRequest(request), getEventSourceListener(eventListener));
  }

  public OllamaCompletionResponse getChatCompletion(OllamaCompletionRequest request) {
    try (var response = httpClient.newCall(buildCompletionHttpRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, OllamaCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama completion for the given request:\n" + request, e);
    }
  }

  private Request buildCompletionHttpRequest(OllamaCompletionRequest request) {
    try {
      var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
      return new Request.Builder()
          .url(host == null ? baseHost + "/api/generate" : host)
          .header("Cache-Control", "no-cache")
          .header("Content-Type", "application/json")
          .header("Accept", request.isStream() ? "text/event-stream" : "text/json")
          .post(RequestBody.create(
              new ObjectMapper().writeValueAsString(request),
              MediaType.parse("application/json")))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private CompletionEventSourceListener getEventSourceListener(
      CompletionEventListener eventListener) {
    return new CompletionEventSourceListener(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          var response = new ObjectMapper().readValue(data, OllamaCompletionResponse.class);
          return response.getResponse();
        } catch (JacksonException e) {
          // ignore
        }
        return "";
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  public static class Builder {

    private String host;
    private Integer port;

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setPort(Integer port) {
      this.port = port;
      return this;
    }

    public OllamaClient build(OkHttpClient.Builder builder) {
      return new OllamaClient(this, builder);
    }

    public OllamaClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
