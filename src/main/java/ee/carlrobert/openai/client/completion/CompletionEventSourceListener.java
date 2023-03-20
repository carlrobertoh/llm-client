package ee.carlrobert.openai.client.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CompletionEventSourceListener extends EventSourceListener {

  private static final Logger LOG = LoggerFactory.getLogger(CompletionEventSourceListener.class);

  private static final String DEFAULT_ERROR_MSG = "Something went wrong. Please try again later.";

  private final OkHttpClient client;
  private final CompletionEventListener listeners;
  private final StringBuilder messageBuilder = new StringBuilder();

  public CompletionEventSourceListener(OkHttpClient client, CompletionEventListener listeners) {
    this.client = client;
    this.listeners = listeners;
  }

  protected abstract String getMessage(String data) throws JsonProcessingException;

  public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
    // TODO
  }

  public void onClosed(@NotNull EventSource eventSource) {
    // TODO
  }

  public void onEvent(
      @NotNull EventSource eventSource,
      String id,
      String type,
      @NotNull String data) {
    try {
      if ("[DONE]".equals(data)) {
        listeners.onComplete(messageBuilder);
        return;
      }

      var message = getMessage(data);
      if (message != null) {
        messageBuilder.append(message);
        listeners.onMessage(message);
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize payload.", e);
    }
  }

  public void onFailure(
      @NotNull EventSource eventSource,
      Throwable ex,
      Response response) {
    if (isRequestCancelled()) {
      listeners.onComplete(messageBuilder);
      return;
    }

    if (ex instanceof SocketTimeoutException) {
      listeners.onFailure("Request timed out. This may be due to the server being overloaded.");
      return;
    }

    try {
      if (response == null) {
        listeners.onFailure(DEFAULT_ERROR_MSG);
        return;
      }

      var body = response.body();
      if (body != null) {
        var error = new ObjectMapper().readValue(body.string(), ApiResponseError.class);
        listeners.onFailure(error.getError().getMessage());
      }
    } catch (IOException e) {
      LOG.error("Something went wrong.", ex);
      listeners.onFailure(DEFAULT_ERROR_MSG);
    }
  }

  private boolean isRequestCancelled() {
    return client.dispatcher().runningCalls().stream().anyMatch(Call::isCanceled);
  }
}
