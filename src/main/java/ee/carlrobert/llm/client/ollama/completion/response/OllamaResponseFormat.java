package ee.carlrobert.llm.client.ollama.completion.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OllamaResponseFormat {
  JSON("json");

  private final String value;

  OllamaResponseFormat(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
