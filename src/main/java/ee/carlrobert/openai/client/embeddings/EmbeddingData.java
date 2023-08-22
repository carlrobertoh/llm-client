package ee.carlrobert.openai.client.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingData {

  private double[] embedding;

  public double[] getEmbedding() {
    return embedding;
  }

  public void setEmbedding(double[] embedding) {
    this.embedding = embedding;
  }
}
