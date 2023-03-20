package ee.carlrobert.openai.client.completion.text;

import ee.carlrobert.openai.client.completion.CompletionModel;

public enum TextCompletionModel implements CompletionModel {
  ADA("text-ada-001", "Ada - Fastest"),
  BABBAGE("text-babbage-001", "Babbage - Powerful"),
  CURIE("text-curie-001", "Curie - Fast and efficient"),
  DAVINCI("text-davinci-003", "Davinci - Most powerful (Default)");

  private final String code;
  private final String description;

  TextCompletionModel(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }
}
