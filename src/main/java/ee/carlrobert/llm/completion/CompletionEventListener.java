package ee.carlrobert.llm.completion;

import ee.carlrobert.llm.client.openai.completion.ErrorDetails;

public interface CompletionEventListener<T> {

  default void onMessage(T message) {
  }

  default void onComplete(StringBuilder messageBuilder) {
  }

  default void onCancelled(StringBuilder messageBuilder) {
  }

  default void onError(ErrorDetails error, Throwable ex) {
  }
}
