package ee.carlrobert.llm.client.ollama.completion.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OllamaTagsResponse {

  private List<OllamaModel> models;

  public List<OllamaModel> getModels() {
    return models;
  }

  public void setModels(List<OllamaModel> models) {
    this.models = models;
  }
}
