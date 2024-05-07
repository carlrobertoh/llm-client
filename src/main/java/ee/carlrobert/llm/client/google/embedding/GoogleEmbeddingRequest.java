package ee.carlrobert.llm.client.google.embedding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionContent;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class GoogleEmbeddingRequest {

  private final GoogleCompletionContent content;
  private final TaskType taskType;
  private final String title;
  private final Integer outputDimensionality;

  public GoogleEmbeddingRequest(Builder builder) {
    this.content = builder.content;
    this.taskType = builder.taskType;
    this.title = builder.title;
    this.outputDimensionality = builder.outputDimensionality;
  }

  public GoogleCompletionContent getContent() {
    return content;
  }

  public TaskType getTaskType() {
    return taskType;
  }

  public String getTitle() {
    return title;
  }

  public Integer getOutputDimensionality() {
    return outputDimensionality;
  }

  /**
   * <a href="https://ai.google.dev/api/rest/v1/TaskType?authuser=1">TaskType</a>.
   */
  public enum TaskType {
    TASK_TYPE_UNSPECIFIED,
    RETRIEVAL_QUERY,
    RETRIEVAL_DOCUMENT,
    SEMANTIC_SIMILARITY,
    CLASSIFICATION,
    CLUSTERING,
    QUESTION_ANSWERING,
    FACT_VERIFICATION
  }

  public static class Builder {

    private GoogleCompletionContent content;
    private TaskType taskType = TaskType.RETRIEVAL_DOCUMENT; // Set default value
    private String title;
    private Integer outputDimensionality;

    public Builder(GoogleCompletionContent content) {
      this.content = content;
    }

    public Builder taskType(TaskType taskType) {
      this.taskType = taskType;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder outputDimensionality(int outputDimensionality) {
      this.outputDimensionality = outputDimensionality;
      return this;
    }

    public GoogleEmbeddingRequest build() {
      return new GoogleEmbeddingRequest(this);
    }
  }

}
