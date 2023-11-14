package ee.carlrobert.llm.client.mixin;

import ee.carlrobert.llm.client.http.Service;

public enum ExternalService implements Service {
  OPENAI("openai.baseUrl"),
  AZURE("azure.openai.baseUrl"),
  YOU("you.baseUrl"),
  LLAMA("llama.baseUrl");

  private final String urlProperty;

  ExternalService(String urlProperty) {
    this.urlProperty = urlProperty;
  }

  public String getUrlProperty() {
    return urlProperty;
  }
}
