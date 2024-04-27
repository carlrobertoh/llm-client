package ee.carlrobert.llm.client.together;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class TogetherClient {

  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
  private static final String BASE_URL = PropertiesLoader.getValue("together.baseUrl");

  private final OkHttpClient httpClient = new OkHttpClient.Builder().build();
  private final String apiKey;

  public TogetherClient(String apiKey) {
    this.apiKey = apiKey;
  }

  public EventSource getCompletionAsync(
      OpenAITextCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient).newEventSource(
        buildTextCompletionRequest(request),
        new OpenAITextCompletionEventSourceListener(eventListener));
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient).newEventSource(
        buildChatCompletionRequest(request),
        new OpenAIChatCompletionEventSourceListener(eventListener));
  }

  private Request buildChatCompletionRequest(OpenAIChatCompletionRequest request) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(BASE_URL + "/v1/chat/completions")
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Request buildTextCompletionRequest(OpenAITextCompletionRequest request) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(BASE_URL + "/v1/completions")
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getRequiredHeaders() {
    return new HashMap<>(Map.of(
        "Authorization", "Bearer " + apiKey,
        "X-LLM-Application-Tag", "codegpt"));
  }
}
