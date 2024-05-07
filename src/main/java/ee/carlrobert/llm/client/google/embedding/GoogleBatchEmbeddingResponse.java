package ee.carlrobert.llm.client.google.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBatchEmbeddingResponse {

  private List<ContentEmbedding> embeddings;

  public List<ContentEmbedding> getEmbeddings() {
    return embeddings;
  }

  public void setEmbeddings(List<ContentEmbedding> embedding) {
    this.embeddings = embedding;
  }

}
