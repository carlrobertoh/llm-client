package ee.carlrobert.openai.client.completion.text;

import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.OpenAICompletionClient;
import java.util.function.Consumer;

public class TextCompletionClient extends OpenAICompletionClient {

  public TextCompletionClient(OpenAIClient client) {
    super(client, "/v1/completions");
  }

  @Override
  protected TextCompletionEventSourceListener getEventListener(
      CompletionEventListener listeners,
      boolean retryOnReadTimeout,
      Consumer<String> onRetry) {
    return new TextCompletionEventSourceListener(listeners, retryOnReadTimeout, onRetry);
  }

  @Override
  public ClientCode getClientCode() {
    return ClientCode.TEXT_COMPLETION;
  }
}
