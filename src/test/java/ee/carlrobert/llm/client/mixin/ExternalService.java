package ee.carlrobert.llm.client.mixin;

import ee.carlrobert.llm.client.http.Service;

public enum ExternalService implements Service {
  CODEGPT("codegpt.baseUrl"),
  OPENAI("openai.baseUrl"),
  ANTHROPIC("anthropic.baseUrl"),
  AZURE("azure.openai.baseUrl"),
  YOU("you.baseUrl"),
  LLAMA("llama.baseUrl"),
  OLLAMA("ollama.baseUrl"),
  GOOGLE("google.baseUrl"),
  MISTRAL("mistral.baseUrl"),
  INCEPTION("inception.baseUrl");

  private final String urlProperty;

  ExternalService(String urlProperty) {
    this.urlProperty = urlProperty;
  }

  public String getUrlProperty() {
    return urlProperty;
  }
}
