package ee.carlrobert.llm.client.llama;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;
import static java.lang.String.format;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionResponse;
import ee.carlrobert.llm.client.llama.completion.LlamaInfillRequest;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
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
  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

  private final OkHttpClient httpClient;
  private final String host;
  private final Integer port;
  private final String apiKey;

  protected LlamaClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.host = builder.host;
    this.port = builder.port;
    this.apiKey = builder.apiKey;
  }

  public EventSource getCodeCompletionAsync(
      LlamaCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildCompletionHttpRequest(request),
            getEventSourceListener(eventListener));
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildChatCompletionRequest(request),
            new OpenAIChatCompletionEventSourceListener(eventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAIChatCompletionRequest request) {
    try (var response =
        httpClient.newCall(buildChatCompletionRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
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
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient).newEventSource(
        buildHttpRequest(request, "/infill"),
        getEventSourceListener(eventListener));
  }

  private Request buildCompletionHttpRequest(LlamaCompletionRequest request) {
    return buildHttpRequest(request, "/completion");
  }

  private Request buildChatCompletionRequest(OpenAIChatCompletionRequest request) {
    try {
      var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
      var url = (host == null ? baseHost : host) + "/v1/chat/completions";
      return new Request.Builder()
          .url(url)
          .header("Cache-Control", "no-cache")
          .header("Content-Type", "application/json")
          .header("Accept", request.isStream() ? "text/event-stream" : "text/json")
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Request buildHttpRequest(LlamaCompletionRequest request, String path) {
    try {
      var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
      var url = (host == null ? baseHost : host) + path;
      Request.Builder builder = new Request.Builder()
          .url(url)
          .header("Cache-Control", "no-cache")
          .header("Content-Type", "application/json")
          .header("Accept", request.isStream() ? "text/event-stream" : "text/json")
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON));
      if (apiKey != null) {
        builder.header("Authorization", "Bearer " + apiKey);
      }
      return builder.build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private CompletionEventSourceListener<String> getEventSourceListener(
      CompletionEventListener<String> eventListener) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          var response = OBJECT_MAPPER.readValue(data, LlamaCompletionResponse.class);
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
    private String apiKey;

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setPort(Integer port) {
      this.port = port;
      return this;
    }

    public Builder setApiKey(String apiKey) {
      this.apiKey = apiKey;
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
