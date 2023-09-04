package ee.carlrobert.llm.client.openai.completion.chat;

import ee.carlrobert.llm.completion.CompletionModel;
import java.util.Arrays;

public enum ChatCompletionModel implements CompletionModel {
  GPT_3_5("gpt-3.5-turbo", "ChatGPT(3.5) - Most capable model (Default)", 4096),
  GPT_3_5_16k("gpt-3.5-turbo-16k", "ChatGPT(3.5) - 16k - Same capabilities as 3.5 but with 4x the context", 16384),
  GPT_4("gpt-4", "ChatGPT(4.0) - Most recent model", 8192),
  GPT_4_32k("gpt-4-32k", "ChatGPT(4.0) - 32k - Same capabilities as 4.0 but with 4x the context", 32768);

  private final String code;
  private final String description;
  private final int maxTokens;

  ChatCompletionModel(String code, String description, int maxTokens) {
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

  static public ChatCompletionModel findByCode(String code) {
    return Arrays.stream(ChatCompletionModel.values())
        .filter(item -> item.getCode().equals(code))
        .findFirst().orElseThrow();
  }
}

