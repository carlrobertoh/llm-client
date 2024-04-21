package ee.carlrobert.llm.client.ollama.completion.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OllamaChatCompletionResponse {

  private String model;
  private String createdAt;
  private OllamaChatCompletionMessageResponse message;
  private Boolean done;
  private Integer promptEvalCount;
  private Integer evalCount;
  private Long promptEvalDuration;
  private Long evalDuration;
  private Long totalDuration;
  private Long loadDuration;

  public OllamaChatCompletionResponse() {
  }

  public OllamaChatCompletionResponse(@NotNull String model,
      @NotNull String createdAt,
      @NotNull OllamaChatCompletionMessageResponse message,
      @NotNull Boolean done,
      @Nullable Integer promptEvalCount,
      @Nullable Integer evalCount,
      @Nullable Long promptEvalDuration,
      @Nullable Long evalDuration,
      @Nullable Long totalDuration,
      @Nullable Long loadDuration) {
    this.model = model;
    this.createdAt = createdAt;
    this.message = message;
    this.done = done;
    this.promptEvalCount = promptEvalCount;
    this.evalCount = evalCount;
    this.promptEvalDuration = promptEvalDuration;
    this.evalDuration = evalDuration;
    this.totalDuration = totalDuration;
    this.loadDuration = loadDuration;
  }

  @NotNull
  public String getModel() {
    return model;
  }

  public void setModel(@NotNull String model) {
    this.model = model;
  }

  @NotNull
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@NotNull String createdAt) {
    this.createdAt = createdAt;
  }

  @NotNull
  public OllamaChatCompletionMessageResponse getMessage() {
    return message;
  }

  public void setMessage(@NotNull OllamaChatCompletionMessageResponse message) {
    this.message = message;
  }

  @NotNull
  public Boolean isDone() {
    return done;
  }

  @Nullable
  public Integer getPromptEvalCount() {
    return promptEvalCount;
  }

  public void setPromptEvalCount(@Nullable Integer promptEvalCount) {
    this.promptEvalCount = promptEvalCount;
  }

  @Nullable
  public Integer getEvalCount() {
    return evalCount;
  }

  public void setEvalCount(@Nullable Integer evalCount) {
    this.evalCount = evalCount;
  }

  @Nullable
  public Long getPromptEvalDuration() {
    return promptEvalDuration;
  }

  public void setPromptEvalDuration(@Nullable Long promptEvalDuration) {
    this.promptEvalDuration = promptEvalDuration;
  }

  @Nullable
  public Long getEvalDuration() {
    return evalDuration;
  }

  public void setEvalDuration(@Nullable Long evalDuration) {
    this.evalDuration = evalDuration;
  }

  @Nullable
  public Long getTotalDuration() {
    return totalDuration;
  }

  public void setTotalDuration(@Nullable Long totalDuration) {
    this.totalDuration = totalDuration;
  }

  @Nullable
  public Long getLoadDuration() {
    return loadDuration;
  }

  public void setLoadDuration(@Nullable Long loadDuration) {
    this.loadDuration = loadDuration;
  }

  public void setDone(@NotNull Boolean done) {
    this.done = done;
  }
}
