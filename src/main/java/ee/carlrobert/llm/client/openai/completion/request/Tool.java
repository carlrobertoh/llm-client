package ee.carlrobert.llm.client.openai.completion.request;

public class Tool {

  private String type;
  private ToolFunction function;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ToolFunction getFunction() {
    return function;
  }

  public void setFunction(ToolFunction function) {
    this.function = function;
  }
}
