package ee.carlrobert.llm.client.codegpt;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSource;

public class CodeGPTClient {

  private static final String BASE_URL = PropertiesLoader.getValue("codegpt.baseUrl");

  private final OpenAIClient client;

  public CodeGPTClient(String apiKey) {
    this(apiKey, new OkHttpClient.Builder());
  }

  public CodeGPTClient(String apiKey, OkHttpClient.Builder httpClientBuilder) {
    this.client = new OpenAIClient.Builder(apiKey).setHost(BASE_URL).build(httpClientBuilder);
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

  private Request buildUserDetailsRequest(String apiKey) {
    return new Request.Builder()
        .url(BASE_URL + "/v1/users/details")
        .header("Authorization", "Bearer " + apiKey)
        .get()
        .build();
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return client.getChatCompletionAsync(
        request,
        getChatCompletionEventSourceListener(eventListener));
  }

  public EventSource getCompletionAsync(
      OpenAITextCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return client.getCompletionAsync(request, getTextCompletionEventSourceListener(eventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAIChatCompletionRequest request) {
    return client.getChatCompletion(request);
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

  private OpenAITextCompletionEventSourceListener getTextCompletionEventSourceListener(
      CompletionEventListener<String> listener) {
    return new OpenAITextCompletionEventSourceListener(listener) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(data, CodeGPTApiResponseError.class).getError();
      }
    };
  }
}
