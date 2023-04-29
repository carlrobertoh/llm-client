package ee.carlrobert.openai.client.completion.text;

import ee.carlrobert.openai.PropertiesLoader;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionClient;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import okhttp3.sse.EventSource;

public class TextCompletionClient extends CompletionClient {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.baseUrl");

  public TextCompletionClient(OpenAIClient client) {
    super(client,
        client.getHost() == null ? BASE_URL : client.getHost(),
        "/v1/completions");
  }

  @Override
  protected TextCompletionEventSourceListener getEventListener(CompletionEventListener listeners) {
    return new TextCompletionEventSourceListener(listeners);
  }

  @Override
  protected ClientCode getClientCode() {
    return ClientCode.TEXT_COMPLETION;
  }

  public EventSource stream(TextCompletionRequest requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }
}
