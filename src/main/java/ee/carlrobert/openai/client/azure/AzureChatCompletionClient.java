package ee.carlrobert.openai.client.azure;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionEventSourceListener;

public class AzureChatCompletionClient extends AzureCompletionClient {

  public AzureChatCompletionClient(OpenAIClient client, AzureClientRequestParams requestParams) {
    super(client,
        requestParams.getResourceName(),
        format("/openai/deployments/%s/chat/completions?api-version=%s",
            requestParams.getDeploymentId(), requestParams.getApiVersion()));
  }

  @Override
  protected CompletionEventSourceListener getEventListener(CompletionEventListener listeners) {
    return new ChatCompletionEventSourceListener(listeners) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return new ObjectMapper().readValue(data, AzureApiResponseError.class).getError();
      }
    };
  }
}
