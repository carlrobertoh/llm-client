package ee.carlrobert.llm.completion;

import ee.carlrobert.llm.client.Client;
import okhttp3.sse.EventSource;

public abstract class CompletionClient {

  protected Client client;

  public CompletionClient(Client client) {
    this.client = client;
  }

  public abstract EventSource getCompletion(CompletionRequest request, CompletionEventListener completionEventListener);

  public abstract CompletionResponse getCompletion(CompletionRequest request);
}
