package ee.carlrobert.openai.client.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.Client;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

  public <T extends CompletionRequest> String call(T requestBody) {
    try (var response = client.getHttpClient().newCall(buildRequest(requestBody)).execute()) {
      return Objects.requireNonNull(response.body()).string();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected <T extends CompletionRequest> okhttp3.Request buildRequest(T requestBody) throws JsonProcessingException {
    var headers = new HashMap<>(getRequiredHeaders());
    if (requestBody.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
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
  }

  protected <T extends CompletionRequest> EventSource createNewEventSource(T requestBody, CompletionEventListener listeners) {
    Request request;
    try {
      request = buildRequest(requestBody);
    } catch (JsonProcessingException e) {
      var error = new RuntimeException("Unable to build request", e);
      listeners.onError(new ErrorDetails(e.getMessage()), error);
      throw error;
    }

    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(
            request,
            getEventListener(listeners, client.isRetryOnReadTimeout(), (response) -> {
              if (retryCounter > MAX_RETRY_COUNT) {
                listeners.onError(new ErrorDetails("The server may be overloaded as the request has timed out for 3 times."), new RuntimeException());
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
