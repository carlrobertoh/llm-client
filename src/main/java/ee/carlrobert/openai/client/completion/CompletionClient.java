package ee.carlrobert.openai.client.completion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.Client;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public abstract class CompletionClient {

  private static final int MAX_RETRY_COUNT = 3;

  private final Client client;
  private final String url;
  private int retryCounter = 0;

  public CompletionClient(Client client, String host, String path) {
    this.client = client;
    this.url = host + path;
  }

  protected abstract Map<String, String> getRequiredHeaders();

  protected abstract CompletionEventSourceListener getEventListener(
      CompletionEventListener listeners,
      boolean retryOnReadTimeout,
      Consumer<String> onRetry);

  public abstract ClientCode getClientCode();

  public <T extends CompletionRequest> EventSource stream(T requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }

  protected <T extends CompletionRequest> okhttp3.Request buildRequest(T requestBody) {
    var headers = new HashMap<>(getRequiredHeaders());
    headers.put("Accept", "text/event-stream");
    try {
      var mapper = new ObjectMapper();
      var map = mapper.readValue(mapper.writeValueAsString(requestBody), new TypeReference<Map<String, Object>>() {});
      var additionalParams = requestBody.getAdditionalParams();
      if (additionalParams != null && !additionalParams.isEmpty()) {
        map.putAll(additionalParams);
      }

      return new Request.Builder()
          .url(url)
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              mapper.writeValueAsString(map),
              MediaType.parse("application/json")))
          .build();
    } catch (Exception ex) {
      throw new RuntimeException("Unable to serialize request payload");
    }
  }

  protected <T extends CompletionRequest> EventSource createNewEventSource(T requestBody, CompletionEventListener listeners) {
    return EventSources.createFactory(client.buildHttpClient())
        .newEventSource(
            buildRequest(requestBody),
            getEventListener(listeners, client.isRetryOnReadTimeout(), (response) -> {
              if (retryCounter > MAX_RETRY_COUNT) {
                listeners.onError(new ErrorDetails("The server may be overloaded as the request has timed out for 3 times."));
                return;
              }

              if (requestBody instanceof ChatCompletionRequest) {
                var body = ((ChatCompletionRequest) requestBody);

                if (retryCounter == 0) {
                  body.addMessage(new ChatCompletionMessage("assistant", response));
                } else {
                  var messages = body.getMessages();
                  var message = messages.get(messages.size() - 1);
                  message.setContent(message.getContent() + response);
                }

                retryCounter = retryCounter + 1;
                createNewEventSource(requestBody, listeners);
              }
              if (requestBody instanceof TextCompletionRequest) {
                listeners.onComplete(new StringBuilder(response));
              }
            }));
  }
}
