package ee.carlrobert.llm.client.ollama.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/*
 * See <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-completion">ollama/api</a>
 */
@JsonInclude(Include.NON_NULL)
public class OllamaEmbeddingRequest {

  private final String model;
  private final String prompt;
  private final OllamaParameters options;

  public OllamaEmbeddingRequest(Builder builder) {
    this.prompt = builder.prompt;
    this.model = builder.model;
    this.options = builder.options;
  }

  public String getModel() {
    return model;
  }

  public String getPrompt() {
    return prompt;
  }

  public OllamaParameters getOptions() {
    return options;
  }

  public static class Builder {

    private final String model;
    private final String prompt;
    private OllamaParameters options = null;

    public Builder(String model, String prompt) {
      this.model = model;
      this.prompt = prompt;
    }

    public OllamaEmbeddingRequest.Builder setOptions(OllamaParameters options) {
      this.options = options;
      return OllamaEmbeddingRequest.Builder.this;
    }

    public OllamaEmbeddingRequest build() {
      return new OllamaEmbeddingRequest(this);
    }
  }
}