package ee.carlrobert.llm.client.you.completion;

import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;

public interface YouCompletionEventListener extends CompletionEventListener<String> {

  default void onSerpResults(List<YouSerpResult> results) {
  }
}
