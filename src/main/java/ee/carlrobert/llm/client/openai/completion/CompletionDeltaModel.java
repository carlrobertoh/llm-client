package ee.carlrobert.llm.client.openai.completion;

import org.jetbrains.annotations.Nullable;

class CompletionDeltaModel {
  @Nullable String reasoningContent;
  @Nullable String content;

  public CompletionDeltaModel(@Nullable String reasoningContent, @Nullable String content) {
    this.reasoningContent = reasoningContent;
    this.content = content;
  }

  @Nullable
  public String getReasoningContent() {
    return reasoningContent;
  }

  @Nullable
  public String getContent() {
    return content;
  }
}
