package ee.carlrobert.openai.client.azure;

import static java.lang.String.format;

import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.text.TextCompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import okhttp3.sse.EventSource;

public class AzureTextCompletionClient extends AzureCompletionClient {

  public AzureTextCompletionClient(OpenAIClient client,
      AzureClientRequestParams requestParams) {
    super(client,
        requestParams.getResourceName(),
        format("/openai/deployments/%s/completions?api-version=%s",
            requestParams.getDeploymentId(), requestParams.getApiVersion()));
  }

  @Override
  protected CompletionEventSourceListener<AzureApiResponseError> getEventListener(
      CompletionEventListener listeners) {
    return new TextCompletionEventSourceListener<>(listeners, AzureApiResponseError.class);
  }

  public EventSource stream(TextCompletionRequest requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }
}
