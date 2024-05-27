package ee.carlrobert.llm.client.codegpt;

public enum PricingPlan {
  ANONYMOUS("Anonymous"), FREE("Free"), INDIVIDUAL("Individual");

  private final String label;

  PricingPlan(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
