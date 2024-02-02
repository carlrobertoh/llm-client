package ee.carlrobert.llm.client.ollama.completion;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OllamaCompletionRequestOptions {

  @JsonProperty("num_predict")
  private int numPredict;
  private double temperature;

  public int getNumPredict() {
    return numPredict;
  }

  public void setNumPredict(int numPredict) {
    this.numPredict = numPredict;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }
}
