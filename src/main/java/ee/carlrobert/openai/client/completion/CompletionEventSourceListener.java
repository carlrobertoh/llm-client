package ee.carlrobert.openai.client.completion;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import okhttp3.Response;
import okhttp3.internal.http2.StreamResetException;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CompletionEventSourceListener extends EventSourceListener {

  private static final Logger LOG = LoggerFactory.getLogger(CompletionEventSourceListener.class);

  private final CompletionEventListener listeners;
  private final StringBuilder messageBuilder = new StringBuilder();

  public CompletionEventSourceListener(CompletionEventListener listeners) {
    this.listeners = listeners;
  }

  protected abstract String getMessage(String data) throws JsonProcessingException;

  protected abstract ErrorDetails getErrorDetails(String data) throws JsonProcessingException;

  public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
    LOG.info("Request opened.");
  }

  public void onClosed(@NotNull EventSource eventSource) {
    LOG.info("Request closed.");
    listeners.onComplete(messageBuilder);
  }

  public void onEvent(
      @NotNull EventSource eventSource,
      String id,
      String type,
      @NotNull String data) {
    try {
      // Redundant end signal so just ignore
      if ("[DONE]".equals(data)) {
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
    if (ex instanceof StreamResetException) {
      LOG.info("Stream was cancelled");
      listeners.onComplete(messageBuilder);
      return;
    }

    if (ex instanceof SocketTimeoutException) {
      listeners.onError(
          new ErrorDetails("Request timed out. This may be due to the server being overloaded."));
      return;
    }

    try {
      if (response == null) {
        throw new IOException(ex);
      }

      var body = response.body();
      if (body != null) {
        var jsonBody = body.string();
        var errorDetails = getErrorDetails(jsonBody);
        if (errorDetails == null ||
            errorDetails.getMessage() == null || errorDetails.getMessage().isEmpty()) {
          listeners.onError(new ErrorDetails(
              format("Unknown API response. Code: %s, Body: %s", response.code(), jsonBody)));
        } else {
          listeners.onError(errorDetails);
        }
      }
    } catch (IOException e) {
      LOG.error("Something went wrong.", e);
      listeners.onError(ErrorDetails.DEFAULT_ERROR);
    }
  }
}
