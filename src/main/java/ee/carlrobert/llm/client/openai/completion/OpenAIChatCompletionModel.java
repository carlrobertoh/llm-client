package ee.carlrobert.llm.client.openai.completion;

import ee.carlrobert.llm.completion.CompletionModel;
import java.util.Arrays;

public enum OpenAIChatCompletionModel implements CompletionModel {
  GPT_3_5("gpt-3.5-turbo", "GPT-3.5 (4k)", 4096),
  GPT_3_5_1106_16k("gpt-3.5-turbo-1106", "GPT-3.5 Turbo (Legacy) (16k)", 16384),
  GPT_3_5_0125_16k("gpt-3.5-turbo-0125", "GPT-3.5 Turbo (16k)", 16384),
  GPT_4("gpt-4", "GPT-4 (8k)", 8192),
  GPT_4_32k("gpt-4-32k", "GPT-4 (32k)", 32768),
  GPT_4_1106_128k("gpt-4-1106-preview", "GPT-4 Turbo (Legacy) (128k)", 128000),
  GPT_4_0125_128k("gpt-4-0125-preview", "GPT-4 Turbo (128k)", 128000),
  GPT_4_VISION_PREVIEW("gpt-4-vision-preview", "GPT-4 Vision Preview (128k)", 128000),
  GPT_4_O_MINI("gpt-4o-mini", "GPT-4o mini (128k)", 128000),
  GPT_4_O("gpt-4o", "GPT-4o (128k)", 128000),
  O_1_MINI("o1-mini", "o1-mini", 128000),
  O_1_PREVIEW("o1-preview", "o1-preview", 128000);

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

  @Override
  public String toString() {
    return description;
  }

  public static OpenAIChatCompletionModel findByCode(String code) {
    return Arrays.stream(OpenAIChatCompletionModel.values())
        .filter(item -> item.getCode().equals(code))
        .findFirst().orElseThrow();
  }
}

