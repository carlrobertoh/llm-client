package ee.carlrobert.llm.client.openai.completion.chat;

import ee.carlrobert.llm.completion.CompletionModel;
import java.util.Arrays;

public enum OpenAIChatCompletionModel implements CompletionModel {
  GPT_3_5("gpt-3.5-turbo", "GPT-3.5 (4k)", 4096),
  GPT_3_5_16k("gpt-3.5-turbo-16k", "GPT-3.5 (16k)", 16384),
  GPT_4("gpt-4", "GPT-4 (8k)", 8192),
  GPT_4_32k("gpt-4-32k", "GPT-4 (32k)", 32768);

  private final String code;
  private final String description;
  private final int maxTokens;

  OpenAIChatCompletionModel(String code, String description, int maxTokens) {
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

  static public OpenAIChatCompletionModel findByCode(String code) {
    return Arrays.stream(OpenAIChatCompletionModel.values())
        .filter(item -> item.getCode().equals(code))
        .findFirst().orElseThrow();
  }
}

