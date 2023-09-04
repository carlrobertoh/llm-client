package ee.carlrobert.llm.client.openai.completion.chat;

import ee.carlrobert.llm.client.ClientCode;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionClient;
import java.util.function.Consumer;

public class ChatCompletionClient extends OpenAICompletionClient {

  public ChatCompletionClient(OpenAIClient client) {
    super(client, "/v1/chat/completions");
  }

  @Override
  protected ChatCompletionEventSourceListener getEventListener(
      CompletionEventListener listeners,
      boolean retryOnReadTimeout,
      Consumer<String> onRetry) {
    return new ChatCompletionEventSourceListener(listeners, retryOnReadTimeout, onRetry);
  }

  @Override
  public ClientCode getClientCode() {
    return ClientCode.CHAT_COMPLETION;
  }
}
