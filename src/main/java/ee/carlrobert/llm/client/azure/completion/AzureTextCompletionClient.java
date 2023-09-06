package ee.carlrobert.llm.client.azure.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.azure.AzureClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.text.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.text.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.text.response.OpenAITextCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.io.IOException;
import java.util.Objects;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class AzureTextCompletionClient extends AzureCompletionClient {

  public AzureTextCompletionClient(AzureClient client) {
    super(client, "/openai/deployments/%s/completions?api-version=%s");
  }

  @Override
  public EventSource getCompletion(CompletionRequest completionRequest, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(buildHttpRequest((OpenAITextCompletionRequest) completionRequest), getEventSourceListener(completionEventListener));
  }

  @Override
  public OpenAITextCompletionResponse getCompletion(CompletionRequest request) {
    try (var response = client.getHttpClient().newCall(buildHttpRequest((OpenAITextCompletionRequest) request)).execute()) {
      return new ObjectMapper().readValue(Objects.requireNonNull(response.body()).string(), OpenAITextCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private CompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners) {

    return new OpenAITextCompletionEventSourceListener(listeners) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return new ObjectMapper().readValue(data, AzureApiResponseError.class).getError();
      }
    };
  }
}
