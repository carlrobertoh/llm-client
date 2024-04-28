package ee.carlrobert.llm.client.codegpt;

public class CodeGPTModel {

  private final String name;
  private final String code;
  private final ModelType type;

  public CodeGPTModel(
      String name,
      String code,
      ModelType type) {
    this.name = name;
    this.code = code;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public ModelType getType() {
    return type;
  }

  public enum ModelType {
    CHAT, CODE
  }
}
