package ee.carlrobert.llm.client.openai.embeddings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Embedding {

  private final double[] embedding;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Embedding(@JsonProperty("error") double[] embedding) {
    this.embedding = embedding;
  }

  public double[] getEmbedding() {
    return embedding;
  }
}
