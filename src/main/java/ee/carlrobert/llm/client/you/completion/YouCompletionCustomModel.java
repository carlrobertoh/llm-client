package ee.carlrobert.llm.client.you.completion;

import java.util.Arrays;

public enum YouCompletionCustomModel {
  GPT_4("gpt_4", "GPT-4"),
  GPT_4_TURBO("gpt_4_turbo", "GPT-4 Turbo"),
  CLAUDE_INSTANT("claude_instant", "Claude Instant"),
  CLAUDE_2("claude_2", "Claude 2"),
  GEMINI_PRO("gemini_pro", "Gemini Pro"),
  ZEPHYR("zephyr", "Zephyr (uncensored)");

  private final String model;
  private final String description;

  YouCompletionCustomModel(String model, String description) {
    this.model = model;
    this.description = description;
  }

  public String getModel() {
    return model;
  }

  public String getDescription() {
    return description;
  }

  public String toString() {
    return model;
  }

  public static YouCompletionCustomModel findByCode(String code) {
    return Arrays.stream(YouCompletionCustomModel.values())
        .filter(item -> item.getModel().equals(code))
        .findFirst().orElseThrow();
  }
}
