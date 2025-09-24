package ee.carlrobert.llm.completion;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;
import okhttp3.Response;
import okhttp3.internal.http2.StreamResetException;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CompletionEventSourceListener<T> extends EventSourceListener {

  private static final Logger LOG = LoggerFactory.getLogger(CompletionEventSourceListener.class);

  protected final CompletionEventListener<T> listener;
  private final StringBuilder messageBuilder = new StringBuilder();
  private final boolean retryOnReadTimeout;
  private final Consumer<String> onRetry;

  public CompletionEventSourceListener(CompletionEventListener<T> listener) {
    this(listener, false, null);
  }

  public CompletionEventSourceListener(CompletionEventListener<T> listener,
      boolean retryOnReadTimeout, Consumer<String> onRetry) {
    this.listener = listener;
    this.retryOnReadTimeout = retryOnReadTimeout;
    this.onRetry = onRetry;
  }

  protected abstract T getMessage(String data) throws JsonProcessingException;

  protected abstract ErrorDetails getErrorDetails(String data) throws JsonProcessingException;

  public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
    LOG.info("Request opened.");
    listener.onOpen();
  }

  public void onClosed(@NotNull EventSource eventSource) {
    LOG.info("Request closed.");
    listener.onComplete(messageBuilder);
  }

  public void onEvent(
      @NotNull EventSource eventSource,
      String id,
      String type,
      @NotNull String data) {
    try {
      listener.onEvent(data);

      // Redundant end signal so just ignore
      if ("[DONE]".equals(data)) {
        return;
      }

      var message = getMessage(data);
      if (message != null) {
        messageBuilder.append(message);
        listener.onMessage(message, eventSource);
        listener.onMessage(message, data, eventSource);
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize payload.", e);
    }
  }

  public void onFailure(
      @NotNull EventSource eventSource,
      Throwable throwable,
      Response response) {
    if (throwable instanceof StreamResetException
        || (throwable instanceof SocketException
        && "Socket closed".equals(throwable.getMessage()))) {
      LOG.info("Stream was cancelled");
      listener.onCancelled(messageBuilder);
      return;
    }

    if (throwable instanceof SocketTimeoutException) {
      if (retryOnReadTimeout) {
        LOG.info("Retrying request.");
        onRetry.accept(messageBuilder.toString());
        return;
      }

      listener.onError(
          new ErrorDetails("Request timed out. This may be due to the server being overloaded."),
          throwable);
      return;
    }

    try {
      if (response == null) {
        listener.onError(new ErrorDetails(throwable.getMessage()), throwable);
        return;
      }

      var body = response.body();
      if (body != null) {
        var jsonBody = body.string();
        try {
          var errorDetails = getErrorDetails(jsonBody);
          if (errorDetails == null
              || errorDetails.getMessage() == null
              || errorDetails.getMessage().isEmpty()) {
            listener.onError(toUnknownErrorResponse(response, jsonBody), new RuntimeException());
          } else {
            listener.onError(errorDetails, new RuntimeException());
          }
        } catch (JsonProcessingException e) {
          LOG.error("Could not serialize error response", throwable);
          listener.onError(toUnknownErrorResponse(response, jsonBody), e);
        }
      }
    } catch (IOException ex) {
      listener.onError(new ErrorDetails(ex.getMessage()), ex);
    }
  }

  private ErrorDetails toUnknownErrorResponse(Response response, String jsonBody) {
    return new ErrorDetails(
        format("Unknown API response. Code: %s, Body: %s", response.code(), jsonBody));
  }
}
