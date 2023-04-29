package ee.carlrobert.openai.client.azure;

import static java.lang.String.format;

import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import okhttp3.sse.EventSource;

public class AzureChatCompletionClient extends AzureCompletionClient {

  public AzureChatCompletionClient(OpenAIClient client, AzureClientRequestParams requestParams) {
    super(client,
        requestParams.getResourceName(),
        format("/openai/deployments/%s/chat/completions?api-version=%s",
            requestParams.getDeploymentId(), requestParams.getApiVersion()));
  }

  @Override
  protected CompletionEventSourceListener getEventListener(CompletionEventListener listeners) {
    return new ChatCompletionEventSourceListener(listeners);
  }

  public EventSource stream(ChatCompletionRequest requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }
}
