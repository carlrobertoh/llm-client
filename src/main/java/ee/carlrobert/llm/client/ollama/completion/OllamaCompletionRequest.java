package ee.carlrobert.llm.client.ollama.completion;

import ee.carlrobert.llm.completion.CompletionRequest;

public class OllamaCompletionRequest implements CompletionRequest {

  private final String prompt;
  private final String model;
  private String system;
  private boolean stream;
  private OllamaCompletionRequestOptions options;

  public OllamaCompletionRequest(String prompt, String model) {
    this.prompt = prompt;
    this.model = model;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getModel() {
    return model;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public boolean isStream() {
    return stream;
  }

  public void setStream(boolean stream) {
    this.stream = stream;
  }

  public OllamaCompletionRequestOptions getOptions() {
    return options;
  }

  public void setOptions(OllamaCompletionRequestOptions options) {
    this.options = options;
  }
}
