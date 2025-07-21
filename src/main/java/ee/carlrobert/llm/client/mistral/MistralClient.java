package ee.carlrobert.llm.client.mistral;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.codegpt.CodeGPTApiResponseError;
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
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
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

public class MistralClient {

  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
  private final String host = PropertiesLoader.getValue("mistral.baseUrl");
  private final OkHttpClient httpClient;
  private final String apiKey;

  public MistralClient(String apiKey, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.apiKey = apiKey;
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return createNewEventSource(
        buildChatCompletionRequest(request),
        getChatCompletionEventSourceListener(eventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAIChatCompletionRequest request) {
    try (var response = httpClient.newCall(buildChatCompletionRequest(request)).execute()) {
      if (!response.isSuccessful()) {
        var body = response.body();
        if (body == null) {
          throw new RuntimeException("Unable to get response body");
        }

        throw OBJECT_MAPPER.readValue(body.string(), CodeGPTException.class);
      }

      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException("Unable to get chat completion", e);
    }
  }

  public EventSource getCodeCompletionAsync(
      OpenAITextCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return createNewEventSource(
        buildCodeCompletionRequest(request),
        getCodeCompletionEventSourceListener(eventListener));
  }

  private EventSource createNewEventSource(
      Request request,
      EventSourceListener eventSourceListener) {
    return EventSources.createFactory(httpClient).newEventSource(request, eventSourceListener);
  }

  private Request buildChatCompletionRequest(OpenAIChatCompletionRequest request) {
    var headers = new HashMap<>(getHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      var overriddenPath = request.getOverriddenPath();
      return new Request.Builder()
          .url(host + (overriddenPath == null ? "/v1/chat/completions" : overriddenPath))
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Request buildCodeCompletionRequest(OpenAITextCompletionRequest request) {
    var headers = new HashMap<>(getHeaders());
    try {
      return new Request.Builder()
          .url(host + "/v1/fim/completions")
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getHeaders() {
    var headers = new HashMap<>(Map.of("X-LLM-Application-Tag", "codegpt"));
    if (apiKey != null && !apiKey.isEmpty()) {
      headers.put("Authorization", "Bearer " + apiKey);
    }
    return headers;
  }

  private OpenAIChatCompletionEventSourceListener getChatCompletionEventSourceListener(
      CompletionEventListener<String> listener) {
    return new OpenAIChatCompletionEventSourceListener(listener) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(data, CodeGPTApiResponseError.class).getError();
      }
    };
  }

  private OpenAIChatCompletionEventSourceListener getCodeCompletionEventSourceListener(
      CompletionEventListener<String> listener) {
    return new OpenAIChatCompletionEventSourceListener(listener) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(data, CodeGPTApiResponseError.class).getError();
      }
    };
  }
}