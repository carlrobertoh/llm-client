package ee.carlrobert.llm.client.inception;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.inception.request.InceptionApplyRequest;
import ee.carlrobert.llm.client.inception.request.InceptionFIMRequest;
import ee.carlrobert.llm.client.inception.request.InceptionNextEditRequest;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.response.OpenAITextCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
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

public class InceptionClient {

  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
  private final OkHttpClient httpClient;
  private final String apiKey;
  private final String host;

  private InceptionClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.apiKey = builder.apiKey;
    this.host = builder.host;
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAIChatCompletionRequest request) {
    try (var response = httpClient
        .newCall(buildPostRequest("/v1/chat/completions", request))
        .execute()) {
      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildChatCompletionRequest(request),
            new OpenAIChatCompletionEventSourceListener(eventListener));
  }

  public EventSource getFimCompletionAsync(
      InceptionFIMRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildFimRequest(request),
            new OpenAITextCompletionEventSourceListener(eventListener));
  }

  public OpenAIChatCompletionResponse getApplyEditCompletion(InceptionApplyRequest request) {
    try (var response = httpClient
        .newCall(buildPostRequest("/v1/apply/completions", request))
        .execute()) {
      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public OpenAIChatCompletionResponse getNextEditCompletion(InceptionNextEditRequest request) {
    try (var response = httpClient
        .newCall(buildPostRequest("/v1/edit/completions", request))
        .execute()) {
      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Request buildPostRequest(String path, Object requestBody) {
    var headers = new HashMap<>(getHeaders());
    headers.put("Content-Type", "application/json");
    try {
      return new Request.Builder()
          .url(host + path)
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              OBJECT_MAPPER.writeValueAsString(requestBody),
              APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Request buildFimRequest(InceptionFIMRequest request) {
    var headers = new HashMap<>(getHeaders());
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "text/event-stream");
    try {
      return new Request.Builder()
          .url(host + "/v1/fim/completions")
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              OBJECT_MAPPER.writeValueAsString(request),
              APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process FIM request", e);
    }
  }

  private Request buildChatCompletionRequest(OpenAIChatCompletionRequest request) {
    var headers = new HashMap<>(getHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(host + "/v1/chat/completions")
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              OBJECT_MAPPER.writeValueAsString(request),
              APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getHeaders() {
    var headers = new HashMap<String, String>();
    if (apiKey != null && !apiKey.isEmpty()) {
      headers.put("Authorization", "Bearer " + apiKey);
    }
    return headers;
  }

  public static class Builder {
    private final String apiKey;
    private String host = PropertiesLoader.getValue("inception.baseUrl");

    public Builder(String apiKey) {
      this.apiKey = apiKey;
    }

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public InceptionClient build(OkHttpClient.Builder builder) {
      return new InceptionClient(this, builder);
    }

    public InceptionClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
