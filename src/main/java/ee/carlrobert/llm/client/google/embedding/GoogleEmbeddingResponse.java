package ee.carlrobert.llm.client.google.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleEmbeddingResponse {

  private ContentEmbedding embedding;

  public ContentEmbedding getEmbedding() {
    return embedding;
  }

  public void setEmbedding(ContentEmbedding embedding) {
    this.embedding = embedding;
  }

}
