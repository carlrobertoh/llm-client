package ee.carlrobert.llm.client.llama;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionResponse;
import ee.carlrobert.llm.client.llama.completion.LlamaInfillRequest;
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

public class LlamaClient {

  private static final String BASE_URL = PropertiesLoader.getValue("llama.baseUrl");

  private final OkHttpClient httpClient;
  private final String host;
  private final Integer port;

  protected LlamaClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.host = builder.host;
    this.port = builder.port;
  }

  public EventSource getChatCompletionAsync(
      LlamaCompletionRequest request,
      CompletionEventListener eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(buildCompletionHttpRequest(request), getEventSourceListener(eventListener));
  }

  public LlamaCompletionResponse getChatCompletion(LlamaCompletionRequest request) {
    try (var response = httpClient.newCall(buildCompletionHttpRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, LlamaCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get llama completion for the given request:\n" + request, e);
    }
  }

  public LlamaCompletionResponse getInfill(LlamaInfillRequest request) {
    try (var response = httpClient.newCall(buildHttpRequest(request, "/infill")).execute()) {
      return DeserializationUtil.mapResponse(response, LlamaCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get llama completion for the given request:\n" + request, e);
    }
  }

  public EventSource getInfillAsync(
      LlamaInfillRequest request,
      CompletionEventListener eventListener) {
    return EventSources.createFactory(httpClient).newEventSource(
        buildHttpRequest(request, "/infill"),
        getEventSourceListener(eventListener));
  }

  private Request buildCompletionHttpRequest(LlamaCompletionRequest request) {
    return buildHttpRequest(request, "/completion");
  }

  private Request buildHttpRequest(LlamaCompletionRequest request, String path) {
    try {
      var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
      return new Request.Builder()
          .url(host == null ? baseHost + path : host)
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
          var response = new ObjectMapper().readValue(data, LlamaCompletionResponse.class);
          return response.getContent();
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

    public LlamaClient build(OkHttpClient.Builder builder) {
      return new LlamaClient(this, builder);
    }

    public LlamaClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
