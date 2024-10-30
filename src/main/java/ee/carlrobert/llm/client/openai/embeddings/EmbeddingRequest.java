package ee.carlrobert.llm.client.openai.embeddings;

import java.util.List;

public class EmbeddingRequest {

  private List<String> input;
  private String model = "text-embedding-3-large";

  public EmbeddingRequest() {
  }

  public EmbeddingRequest(String model, List<String> input) {
    this.model = model;
    this.input = input;
  }

  public List<String> getInput() {
    return input;
  }

  public void setInput(List<String> input) {
    this.input = input;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }
}
