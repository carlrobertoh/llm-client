package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.text.request.OpenAITextCompletionRequest;
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

  protected static final int MAX_RETRY_COUNT = 3;

  protected Client client;
  protected int retryCounter = 0;
  private String url;

  public CompletionClient(Client client, String host, String path) {
    this.client = client;
    this.url = host + path;
  }

  public CompletionClient(Client client) {
    this.client = client;
  }

  protected abstract Map<String, String> getRequiredHeaders();

  protected abstract CompletionEventSourceListener getEventListener(
      CompletionEventListener listeners,
      boolean retryOnReadTimeout,
      Consumer<String> onRetry);

  public <T extends OpenAICompletionRequest> EventSource stream(T requestBody, CompletionEventListener listeners) {
    return createNewEventSource(requestBody, listeners);
  }

  // TODO: Remove OpenAI logic
  public <T extends OpenAICompletionRequest> String call(T requestBody) {
    try (var response = client.getHttpClient().newCall(buildRequest(requestBody)).execute()) {
      return Objects.requireNonNull(response.body()).string();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected <T extends OpenAICompletionRequest> Request buildRequest(T requestBody) throws JsonProcessingException {
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

  protected <T extends OpenAICompletionRequest> EventSource createNewEventSource(T requestBody, CompletionEventListener listeners) {
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

              if (requestBody instanceof OpenAIChatCompletionRequest) {
                var body = ((OpenAIChatCompletionRequest) requestBody);

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
              if (requestBody instanceof OpenAITextCompletionRequest) {
                listeners.onComplete(new StringBuilder(response));
              }
            }));
  }
}
