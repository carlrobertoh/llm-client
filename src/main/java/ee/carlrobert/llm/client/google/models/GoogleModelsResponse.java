package ee.carlrobert.llm.client.google.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleModelsResponse {

  private List<GeminiModelDetails> models;
  private String nextPageToken;

  public List<GeminiModelDetails> getModels() {
    return models;
  }

  public void setModels(List<GeminiModelDetails> models) {
    this.models = models;
  }

  public String getNextPageToken() {
    return nextPageToken;
  }

  public void setNextPageToken(String nextPageToken) {
    this.nextPageToken = nextPageToken;
  }

  public static class GeminiModelDetails {

    private String name;
    private String baseModelId;
    private String version;
    private String displayName;
    private String description;
    private int inputTokenLimit;
    private int outputTokenLimit;
    private List<String> supportedGenerationMethods;
    private double temperature;
    private double topP;
    private int topK;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getBaseModelId() {
      return baseModelId;
    }

    public void setBaseModelId(String baseModelId) {
      this.baseModelId = baseModelId;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public int getInputTokenLimit() {
      return inputTokenLimit;
    }

    public void setInputTokenLimit(int inputTokenLimit) {
      this.inputTokenLimit = inputTokenLimit;
    }

    public int getOutputTokenLimit() {
      return outputTokenLimit;
    }

    public void setOutputTokenLimit(int outputTokenLimit) {
      this.outputTokenLimit = outputTokenLimit;
    }

    public List<String> getSupportedGenerationMethods() {
      return supportedGenerationMethods;
    }

    public void setSupportedGenerationMethods(List<String> supportedGenerationMethods) {
      this.supportedGenerationMethods = supportedGenerationMethods;
    }

    public double getTemperature() {
      return temperature;
    }

    public void setTemperature(double temperature) {
      this.temperature = temperature;
    }

    public double getTopP() {
      return topP;
    }

    public void setTopP(double topP) {
      this.topP = topP;
    }

    public int getTopK() {
      return topK;
    }

    public void setTopK(int topK) {
      this.topK = topK;
    }
  }
}
