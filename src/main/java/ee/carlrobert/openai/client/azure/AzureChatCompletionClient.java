package ee.carlrobert.openai.client.azure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.AzureClient;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionEventSourceListener;
import java.util.function.Consumer;

public class AzureChatCompletionClient extends AzureCompletionClient {

  public AzureChatCompletionClient(AzureClient client) {
    super(client, "/openai/deployments/%s/chat/completions?api-version=%s");
  }

  @Override
  protected CompletionEventSourceListener getEventListener(CompletionEventListener listeners, boolean retryOnReadTimeout, Consumer<String> onRetry) {
    return new ChatCompletionEventSourceListener(listeners, retryOnReadTimeout, onRetry) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return new ObjectMapper().readValue(data, AzureApiResponseError.class).getError();
      }
    };
  }

  @Override
  public ClientCode getClientCode() {
    return ClientCode.AZURE_CHAT_COMPLETION;
  }
}
