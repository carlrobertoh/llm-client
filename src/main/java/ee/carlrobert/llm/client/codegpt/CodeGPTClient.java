package ee.carlrobert.llm.client.codegpt;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.codegpt.request.AutoApplyRequest;
import ee.carlrobert.llm.client.codegpt.request.CodeCompletionRequest;
import ee.carlrobert.llm.client.codegpt.request.InlineEditRequest;
import ee.carlrobert.llm.client.codegpt.request.chat.ChatCompletionRequest;
import ee.carlrobert.llm.client.codegpt.request.prediction.AutocompletionPredictionRequest;
import ee.carlrobert.llm.client.codegpt.request.prediction.DirectPredictionRequest;
import ee.carlrobert.llm.client.codegpt.request.prediction.PastePredictionRequest;
import ee.carlrobert.llm.client.codegpt.response.AutoApplyResponse;
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException;
import ee.carlrobert.llm.client.codegpt.response.PredictionResponse;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeGPTClient {

  private static final Logger LOG = LoggerFactory.getLogger(CodeGPTClient.class);

  private static final String BASE_URL = PropertiesLoader.getValue("codegpt.baseUrl");
  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

  private final OkHttpClient httpClient;
  private final String apiKey;

  public CodeGPTClient(String apiKey) {
    this(apiKey, new OkHttpClient.Builder());
  }

  public CodeGPTClient(String apiKey, OkHttpClient.Builder httpClientBuilder) {
    this.apiKey = apiKey;
    this.httpClient = httpClientBuilder.build();
  }

  public CodeGPTUserDetails getUserDetails(String apiKey) {
    try (var response = httpClient.newCall(buildUserDetailsRequest(apiKey)).execute()) {
      return DeserializationUtil.mapResponse(response, CodeGPTUserDetails.class);
    } catch (IOException e) {
      throw new RuntimeException("Unable to fetch user details", e);
    }
  }

  public EventSource getChatCompletionAsync(
      ChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return createNewEventSource(
        buildChatCompletionRequest(request),
        getChatCompletionEventSourceListener(eventListener));
  }

  public EventSource getCodeCompletionAsync(
      CodeCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return createNewEventSource(
        buildCodeCompletionRequest(request),
        getCodeCompletionEventSourceListener(eventListener));
  }

  public EventSource getInlineEditAsync(
      InlineEditRequest request,
      CompletionEventListener<String> eventListener) {
    return createNewEventSource(
        buildInlineEditRequest(request),
        getChatCompletionEventSourceListener(eventListener));
  }

  public AutoApplyResponse applyChanges(AutoApplyRequest request) {
    try (var response = httpClient.newCall(buildApplyRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, AutoApplyResponse.class);
    } catch (IOException e) {
      throw new RuntimeException("Unable to apply suggested changes", e);
    }
  }

  public OpenAIChatCompletionResponse getChatCompletion(ChatCompletionRequest request) {
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

  public Request buildAutocompletionPredictionRequest(AutocompletionPredictionRequest request) {
    return buildPredictionRequest("/v1/predictions/autocompletion", request);
  }

  public Request buildLookupPredictionRequest(AutocompletionPredictionRequest request) {
    return buildPredictionRequest("/v1/predictions/lookup", request);
  }

  public Request buildDirectPredictionRequest(DirectPredictionRequest request) {
    return buildPredictionRequest("/v1/predictions/direct", request);
  }

  public Request buildPastePredictionRequest(PastePredictionRequest request) {
    return buildPredictionRequest("/v1/predictions/paste", request);
  }

  public PredictionResponse getPrediction(Call call) {
    try (var response = call.execute()) {
      return DeserializationUtil.mapResponse(response, PredictionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException("Unable to get prediction response", e);
    }
  }

  public Call createNewCall(Request request) {
    return httpClient.newCall(request);
  }

  private EventSource createNewEventSource(
      Request request,
      EventSourceListener eventSourceListener) {
    return EventSources.createFactory(httpClient).newEventSource(request, eventSourceListener);
  }

  private Request buildChatCompletionRequest(ChatCompletionRequest request) {
    var headers = new HashMap<>(getRequiredHeaders());
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

  private Request buildInlineEditRequest(InlineEditRequest request) {
    var headers = new HashMap<>(getRequiredHeaders());
    try {
      return new Request.Builder()
          .url(BASE_URL + "/v1/inline-edit")
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process inline edit request", e);
    }
  }

  private Request buildUserDetailsRequest(String apiKey) {
    return new Request.Builder()
        .url(BASE_URL + "/v1/users/details")
        .header("Authorization", "Bearer " + apiKey)
        .get()
        .build();
  }

  private Request buildApplyRequest(AutoApplyRequest request) {
    try {
      return new Request.Builder()
          .url(BASE_URL + "/v1/code/apply")
          .header("Authorization", "Bearer " + apiKey)
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to build file diff request", e);
    }
  }

  private Request buildPredictionRequest(String url, Object request) {
    try {
      return new Request.Builder()
          .url(BASE_URL + url)
          .header("Authorization", "Bearer " + apiKey)
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to build generic prediction request", e);
    }
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