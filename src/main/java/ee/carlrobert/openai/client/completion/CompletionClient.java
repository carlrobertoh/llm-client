package ee.carlrobert.openai.client.completion;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.Client;
import ee.carlrobert.openai.client.ClientCode;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public abstract class CompletionClient {

  private final Client client;
  private final String url;

  public CompletionClient(Client client, String host, String path) {
    this.client = client;
    this.url = host + path;
  }

  protected abstract Map<String, String> getRequiredHeaders();

  protected abstract CompletionEventSourceListener getEventListener(
      CompletionEventListener listeners);

  public abstract ClientCode getClientCode();

  public <T extends CompletionRequest> EventSource stream(
      T requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }

  protected <T> okhttp3.Request buildRequest(T requestBody) {
    var headers = new HashMap<>(getRequiredHeaders());
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
    return EventSources.createFactory(client.buildHttpClient())
        .newEventSource(
            buildRequest(requestBody),
            getEventListener(listeners));
  }
}
