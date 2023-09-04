package ee.carlrobert.llm.client.azure.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.azure.AzureClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.text.TextCompletionEventSourceListener;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.util.function.Consumer;

public class AzureTextCompletionClient extends AzureCompletionClient {

  public AzureTextCompletionClient(AzureClient client) {
    super(client, "/openai/deployments/%s/completions?api-version=%s");
  }

  @Override
  protected CompletionEventSourceListener getEventListener(CompletionEventListener listeners, boolean retryOnReadTimeout, Consumer<String> onRetry) {

    return new TextCompletionEventSourceListener(listeners, retryOnReadTimeout, onRetry) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return new ObjectMapper().readValue(data, AzureApiResponseError.class).getError();
      }
    };
  }
}
