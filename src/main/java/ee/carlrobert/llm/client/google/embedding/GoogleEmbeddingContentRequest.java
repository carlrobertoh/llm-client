package ee.carlrobert.llm.client.google.embedding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionContent;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class GoogleEmbeddingContentRequest extends GoogleEmbeddingRequest {

  private String model;

  public GoogleEmbeddingContentRequest(String content, String model) {
    this(new Builder(new GoogleCompletionContent(List.of(content))), model);
  }

  public GoogleEmbeddingContentRequest(List<String> contents, String model) {
    this(new Builder(new GoogleCompletionContent(contents)), model);
  }

  public GoogleEmbeddingContentRequest(Builder builder, String model) {
    super(builder);
    this.model = model;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }
}
