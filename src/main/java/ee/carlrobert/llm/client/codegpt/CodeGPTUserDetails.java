package ee.carlrobert.llm.client.codegpt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeGPTUserDetails {

  private final String fullName;
  private final PricingPlan pricingPlan;
  private final List<AvailableModel> availableModels;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public CodeGPTUserDetails(
      @JsonProperty("fullName") String fullName,
      @JsonProperty("pricingPlan") PricingPlan pricingPlan,
      @JsonProperty("availableModels") List<AvailableModel> availableModels) {
    this.fullName = fullName;
    this.pricingPlan = pricingPlan;
    this.availableModels = availableModels;
  }

  public String getFullName() {
    return fullName;
  }

  public PricingPlan getPricingPlan() {
    return pricingPlan;
  }

  public List<AvailableModel> getAvailableModels() {
    return availableModels;
  }
}