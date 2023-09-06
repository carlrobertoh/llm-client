package ee.carlrobert.llm.client.openai.completion.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionClient;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.chat.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.io.IOException;
import java.util.Objects;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OpenAIChatCompletionClient extends OpenAICompletionClient {

  public OpenAIChatCompletionClient(OpenAIClient client) {
    super(client, "/v1/chat/completions");
  }

  @Override
  public EventSource getCompletion(CompletionRequest completionRequest, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(buildHttpRequest((OpenAIChatCompletionRequest) completionRequest), getEventSourceListener(completionEventListener));
  }

  @Override
  public OpenAIChatCompletionResponse getCompletion(CompletionRequest request) {
    try (var response = client.getHttpClient().newCall(buildHttpRequest((OpenAIChatCompletionRequest) request)).execute()) {
      return new ObjectMapper().readValue(Objects.requireNonNull(response.body()).string(), OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private OpenAIChatCompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners) {
    return new OpenAIChatCompletionEventSourceListener(listeners);
  }
}
