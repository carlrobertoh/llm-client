package ee.carlrobert.llm.client.openai.completion;

import org.jetbrains.annotations.Nullable;

public class ChatCompletionResponseData {
    @Nullable private final String content;
    @Nullable private final String reasoningContent;

    public ChatCompletionResponseData(@Nullable String content, @Nullable String reasoningContent) {
        this.content = content;
        this.reasoningContent = reasoningContent;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    @Nullable
    public String getReasoningContent() {
        return reasoningContent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (reasoningContent != null && !reasoningContent.isEmpty()) {
            sb.append("<think>").append(reasoningContent).append("</think>\n\n");
        }
        if (content != null) {
            sb.append(content);
        }
        return sb.toString();
    }
} 