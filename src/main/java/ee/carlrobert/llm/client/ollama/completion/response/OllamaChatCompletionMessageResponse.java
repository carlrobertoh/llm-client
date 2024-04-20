package ee.carlrobert.llm.client.ollama.completion.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OllamaChatCompletionMessageResponse {
    private final String role;
    private final String content;
    private final List<String> images;

    public OllamaChatCompletionMessageResponse(@NotNull String role, @NotNull String content, @Nullable List<String> images) {
        this.role = role;
        this.content = content;
        this.images = images;
    }

    @NotNull
    public String getRole() {
        return role;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    @Nullable
    public List<String> getImages() {
        return images;
    }
}
