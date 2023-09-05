package ee.carlrobert.llm.client.openai.completion.chat;

import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionClient;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OpenAIChatCompletionClient extends OpenAICompletionClient {

  public OpenAIChatCompletionClient(OpenAIClient client) {
    super(client, "/v1/chat/completions");
  }

  @Override
  public EventSource stream(CompletionRequest completionRequest, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(buildHttpRequest((OpenAIChatCompletionRequest) completionRequest), getEventSourceListener(completionEventListener));
  }

  @Override
  protected OpenAIChatCompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners) {
    return new OpenAIChatCompletionEventSourceListener(listeners);
  }
}
