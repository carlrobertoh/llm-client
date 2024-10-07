package ee.carlrobert.llm.client.codegpt;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.codegpt.request.CodeCompletionRequest;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
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
import okhttp3.sse.EventSources;

public class CodeGPTClient {

  private static final String BASE_URL = PropertiesLoader.getValue("codegpt.baseUrl");
  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

  private final OpenAIClient openaiClient;
  private final String apiKey;

  public CodeGPTClient(String apiKey) {
    this(apiKey, new OkHttpClient.Builder());
  }

  public CodeGPTClient(String apiKey, OkHttpClient.Builder httpClientBuilder) {
    this.apiKey = apiKey;
    this.openaiClient = new OpenAIClient.Builder(apiKey).setHost(BASE_URL).build(httpClientBuilder);
  }

  public CodeGPTUserDetails getUserDetails(String apiKey) {
    try (var response = new OkHttpClient.Builder().build()
        .newCall(buildUserDetailsRequest(apiKey))
        .execute()) {

      return DeserializationUtil.mapResponse(response, CodeGPTUserDetails.class);
    } catch (IOException e) {
      throw new RuntimeException("Unable to fetch user details", e);
    }
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return openaiClient.getChatCompletionAsync(
        request,
        getChatCompletionEventSourceListener(eventListener));
  }

  public EventSource getCodeCompletionAsync(
      CodeCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(new OkHttpClient.Builder().build())
        .newEventSource(
            buildCodeCompletionRequest(request),
            getCodeCompletionEventSourceListener(eventListener));
  }

  @Deprecated
  public EventSource getCompletionAsync(
      OpenAITextCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return openaiClient.getCompletionAsync(
        request,
        getCodeCompletionEventSourceListener(eventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAIChatCompletionRequest request) {
    return openaiClient.getChatCompletion(request);
  }

  private Request buildCodeCompletionRequest(CodeCompletionRequest request) {
    var headers = new HashMap<>(getRequiredHeaders());
    try {
      return new Request.Builder()
          .url(BASE_URL + "/v1/code/completions")
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Request buildUserDetailsRequest(String apiKey) {
    return new Request.Builder()
        .url(BASE_URL + "/v1/users/details")
        .header("Authorization", "Bearer " + apiKey)
        .get()
        .build();
  }

  private Map<String, String> getRequiredHeaders() {
    var headers = new HashMap<>(Map.of(
        "X-LLM-Application-Tag", "codegpt",
        "Accept", "text/event-stream"));
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

  private OpenAITextCompletionEventSourceListener getCodeCompletionEventSourceListener(
      CompletionEventListener<String> listener) {
    return new OpenAITextCompletionEventSourceListener(listener) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(data, CodeGPTApiResponseError.class).getError();
      }
    };
  }
}
