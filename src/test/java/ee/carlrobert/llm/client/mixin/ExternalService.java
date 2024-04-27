package ee.carlrobert.llm.client.mixin;

import ee.carlrobert.llm.client.http.Service;

public enum ExternalService implements Service {
  OPENAI("openai.baseUrl"),
  ANTHROPIC("anthropic.baseUrl"),
  AZURE("azure.openai.baseUrl"),
  YOU("you.baseUrl"),
  TOGETHER("together.baseUrl"),
  LLAMA("llama.baseUrl"),
  OLLAMA("ollama.baseUrl");

  private final String urlProperty;

  ExternalService(String urlProperty) {
    this.urlProperty = urlProperty;
  }

  public String getUrlProperty() {
    return urlProperty;
  }
}
