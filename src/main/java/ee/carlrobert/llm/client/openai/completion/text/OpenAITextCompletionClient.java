package ee.carlrobert.llm.client.openai.completion.text;

import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionClient;
import ee.carlrobert.llm.client.openai.completion.text.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OpenAITextCompletionClient extends OpenAICompletionClient {

  public OpenAITextCompletionClient(OpenAIClient client) {
    super(client, "/v1/completions");
  }

  @Override
  public EventSource stream(CompletionRequest completionRequest, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(buildHttpRequest((OpenAITextCompletionRequest) completionRequest), getEventSourceListener(completionEventListener));
  }

  @Override
  protected OpenAITextCompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners) {
    return new OpenAITextCompletionEventSourceListener(listeners);
  }
}
