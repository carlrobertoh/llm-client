package ee.carlrobert.llm.client.openai.completion.text;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionClient;
import ee.carlrobert.llm.client.openai.completion.text.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.text.response.OpenAITextCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.io.IOException;
import java.util.Objects;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OpenAITextCompletionClient extends OpenAICompletionClient {

  public OpenAITextCompletionClient(OpenAIClient client) {
    super(client, "/v1/completions");
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

  private OpenAITextCompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners) {
    return new OpenAITextCompletionEventSourceListener(listeners);
  }
}
