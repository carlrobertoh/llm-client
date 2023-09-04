package ee.carlrobert.llm.client.openai.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse {

  private List<EmbeddingData> data;

  public List<EmbeddingData> getData() {
    return data;
  }

  public void setData(List<EmbeddingData> data) {
    this.data = data;
  }
}
