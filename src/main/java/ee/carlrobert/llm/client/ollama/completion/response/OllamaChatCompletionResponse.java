package ee.carlrobert.llm.client.ollama.completion.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OllamaChatCompletionResponse {

    private final String model;
    private final String createdAt;
    private final OllamaChatCompletionMessageResponse message;
    private final Boolean done;
    private final Integer promptEvalCount;
    private final Integer evalCount;
    private final Long promptEvalDuration;
    private final Long evalDuration;

    public OllamaChatCompletionResponse(@NotNull String model,
                                        @NotNull String createdAt,
                                        @NotNull OllamaChatCompletionMessageResponse message,
                                        @NotNull Boolean done,
                                        @Nullable Integer promptEvalCount,
                                        @Nullable Integer evalCount,
                                        @Nullable Long promptEvalDuration,
                                        @Nullable Long evalDuration) {
        this.model = model;
        this.createdAt = createdAt;
        this.message = message;
        this.done = done;
        this.promptEvalCount = promptEvalCount;
        this.evalCount = evalCount;
        this.promptEvalDuration = promptEvalDuration;
        this.evalDuration = evalDuration;
    }

    @NotNull
    public String getModel() {
        return model;
    }

    @NotNull
    public String getCreatedAt() {
        return createdAt;
    }

    @NotNull
    public OllamaChatCompletionMessageResponse getMessage() {
        return message;
    }

    @NotNull
    public Boolean isDone() {
        return done;
    }

    @Nullable
    public Integer getPromptEvalCount() {
        return promptEvalCount;
    }

    @Nullable
    public Integer getEvalCount() {
        return evalCount;
    }

    @Nullable
    public Long getPromptEvalDuration() {
        return promptEvalDuration;
    }

    @Nullable
    public Long getEvalDuration() {
        return evalDuration;
    }
}
