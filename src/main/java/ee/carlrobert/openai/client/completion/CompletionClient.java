package ee.carlrobert.openai.client.completion;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.BaseClient;
import ee.carlrobert.openai.client.OpenAIClient;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public abstract class CompletionClient extends BaseClient {

  private final String url;

  public CompletionClient(OpenAIClient client, String host, String path) {
    super(client);
    this.url = host + path;
  }

  protected abstract CompletionEventSourceListener getEventListener(
      CompletionEventListener listeners);

  public <T extends CompletionRequest> EventSource stream(
      T requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }

  protected <T> okhttp3.Request buildRequest(T requestBody) {
    headers.put("Accept", "text/event-stream");
    try {
      return new Request.Builder()
          .url(url)
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              new ObjectMapper()
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(requestBody),
              MediaType.parse("application/json")))
          .build();
    } catch (Exception ex) {
      throw new RuntimeException("Unable to serialize request payload");
    }
  }

  protected <T> EventSource createNewEventSource(T requestBody, CompletionEventListener listeners) {
    return EventSources.createFactory(buildClient())
        .newEventSource(
            buildRequest(requestBody),
            getEventListener(listeners));
  }
}
