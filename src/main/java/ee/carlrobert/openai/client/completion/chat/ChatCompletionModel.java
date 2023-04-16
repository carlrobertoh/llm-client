package ee.carlrobert.openai.client.completion.chat;

import ee.carlrobert.openai.client.completion.CompletionModel;
import java.util.Arrays;

public enum ChatCompletionModel implements CompletionModel {
  GPT_3_5("gpt-3.5-turbo", "ChatGPT(3.5) - Most capable model (Default)", 4096),
  GPT_3_5_SNAPSHOT("gpt-3.5-turbo-0301", "ChatGPT(3.5) - Snapshot of gpt-3.5-turbo from March 1st 2023", 4096),
  GPT_4("gpt-4", "ChatGPT(4.0) - Most recent model (Requires access)", 8192);

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

