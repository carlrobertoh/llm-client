package ee.carlrobert.llm.client.you.completion;

import java.util.Arrays;

public enum YouCompletionMode {
  DEFAULT("default", false),
  AGENT("agent", false),
  CUSTOM("custom", true),
  RESEARCH("research", false);

  private final String code;
  private final boolean supportCustomModel;

  YouCompletionMode(String code, boolean supportCustomModel) {
    this.code = code;
    this.supportCustomModel = supportCustomModel;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return code.substring(0, 1).toUpperCase() + code.substring(1);
  }

  public boolean isSupportCustomModel() {
    return supportCustomModel;
  }

  public String toString() {
    return code;
  }

  public static YouCompletionMode findByCode(String code) {
    return Arrays.stream(YouCompletionMode.values())
        .filter(item -> item.getCode().equals(code))
        .findFirst().orElseThrow();
  }
}

