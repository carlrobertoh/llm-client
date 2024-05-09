package ee.carlrobert.llm.client.codegpt;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import okhttp3.OkHttpClient;
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
