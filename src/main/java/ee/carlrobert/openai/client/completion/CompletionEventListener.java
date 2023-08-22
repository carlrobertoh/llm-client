package ee.carlrobert.openai.client.completion;

public interface CompletionEventListener {

  default void onMessage(String message) {
  }

  default void onComplete(StringBuilder messageBuilder) {
  }

  default void onError(ErrorDetails error, Throwable ex) {
  }
}
