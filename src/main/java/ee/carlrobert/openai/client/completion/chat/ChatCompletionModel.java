package ee.carlrobert.openai.client.completion.chat;

import ee.carlrobert.openai.client.completion.CompletionModel;

public enum ChatCompletionModel implements CompletionModel {
  GPT_3_5("gpt-3.5-turbo", "ChatGPT(3.5) - Most capable model (Default)"),
  GPT_3_5_SNAPSHOT("gpt-3.5-turbo-0301", "ChatGPT(3.5) - Snapshot of gpt-3.5-turbo from March 1st 2023"),
  GPT_4("gpt-4", "ChatGPT(4.0) - Most recent model (Requires access)");

  private final String code;
  private final String description;

  ChatCompletionModel(String code, String description) {
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

