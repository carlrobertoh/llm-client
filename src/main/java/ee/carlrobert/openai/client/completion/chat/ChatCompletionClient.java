package ee.carlrobert.openai.client.completion.chat;

import ee.carlrobert.openai.PropertiesLoader;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionClient;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import okhttp3.sse.EventSource;

public class ChatCompletionClient extends CompletionClient {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.baseUrl");

  public ChatCompletionClient(OpenAIClient client) {
    super(client,
        client.getHost() == null ? BASE_URL : client.getHost(),
        "/v1/chat/completions");
  }

  @Override
  protected ChatCompletionEventSourceListener getEventListener(CompletionEventListener listeners) {
    return new ChatCompletionEventSourceListener(listeners);
  }

  @Override
  protected ClientCode getClientCode() {
    return ClientCode.CHAT_COMPLETION;
  }

  public EventSource stream(ChatCompletionRequest requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }
}
