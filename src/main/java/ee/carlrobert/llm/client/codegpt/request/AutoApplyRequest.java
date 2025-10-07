package ee.carlrobert.llm.client.codegpt.request;

public class AutoApplyRequest {

  private final String model;
  private final String code;
  private final String update;

  public AutoApplyRequest(String model, String code, String update) {
    this.model = model;
    this.code = code;
    this.update = update;
  }

  public String getModel() {
    return model;
  }

  public String getCode() {
    return code;
  }

  public String getUpdate() {
    return update;
  }
}
