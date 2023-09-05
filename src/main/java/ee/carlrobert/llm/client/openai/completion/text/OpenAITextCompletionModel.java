package ee.carlrobert.llm.client.openai.completion.text;

import ee.carlrobert.llm.completion.CompletionModel;
import java.util.Arrays;

public enum OpenAITextCompletionModel implements CompletionModel {
  ADA("text-ada-001", "Ada - Fastest", 2049),
  BABBAGE("text-babbage-001", "Babbage - Powerful", 2049),
  CURIE("text-curie-001", "Curie - Fast and efficient", 2049),
  DAVINCI("text-davinci-003", "Davinci - Most powerful (Default)", 4097);

  private final String code;
  private final String description;
  private final int maxTokens;

  OpenAITextCompletionModel(String code, String description, int maxTokens) {
    this.code = code;
    this.description = description;
    this.maxTokens = maxTokens;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  static public OpenAITextCompletionModel findByCode(String code) {
    return Arrays.stream(OpenAITextCompletionModel.values())
        .filter(item -> item.getCode().equals(code))
        .findFirst().orElseThrow();
  }
}
