package ee.carlrobert.llm.client.codegpt;

import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
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
    return client.getChatCompletionAsync(request, eventListener);
  }

  public EventSource getCompletionAsync(
      OpenAITextCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return client.getCompletionAsync(request, eventListener);
  }
}
