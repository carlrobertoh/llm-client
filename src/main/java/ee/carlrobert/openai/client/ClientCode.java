package ee.carlrobert.openai.client;

public enum ClientCode {
  CHAT_COMPLETION("chat.completion"),
  TEXT_COMPLETION("text.completion"),
  BILLING("billing");

  private final String code;

  ClientCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
