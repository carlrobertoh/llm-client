package ee.carlrobert.llm.client.openai.completion.request;

public class ToolFunction {

  private String name;
  private String description;
  private ToolFunctionParameters parameters;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ToolFunctionParameters getParameters() {
    return parameters;
  }

  public void setParameters(ToolFunctionParameters parameters) {
    this.parameters = parameters;
  }
}
