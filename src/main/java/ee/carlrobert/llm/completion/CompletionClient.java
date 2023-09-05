package ee.carlrobert.llm.completion;

import ee.carlrobert.llm.client.Client;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Request;
import okhttp3.sse.EventSource;

public abstract class CompletionClient {

  protected Client client;

  public CompletionClient(Client client) {
    this.client = client;
  }

  protected abstract CompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners);

  public abstract EventSource stream(CompletionRequest request, CompletionEventListener completionEventListener);

  public String call(Request request) {
    try (var response = client.getHttpClient().newCall(request).execute()) {
      return Objects.requireNonNull(response.body()).string();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
