package ee.carlrobert.llm.client.openai.completion.request;

import java.util.List;
import java.util.Map;

public class ToolFunctionParameters {

  private String type;
  private Map<String, ?> properties;
  private List<String> required;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, ?> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, ?> properties) {
    this.properties = properties;
  }

  public List<String> getRequired() {
    return required;
  }

  public void setRequired(List<String> required) {
    this.required = required;
  }
}
