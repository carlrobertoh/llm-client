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
  private final List<AvailableModel> toolWindowModels;
  private final String avatarBase64;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public CodeGPTUserDetails(
      @JsonProperty("fullName") String fullName,
      @JsonProperty("pricingPlan") PricingPlan pricingPlan,
      @JsonProperty("availableModels") List<AvailableModel> availableModels,
      @JsonProperty("toolWindowModels") List<AvailableModel> toolWindowModels,
      @JsonProperty("avatarBase64") String avatarBase64) {
    this.fullName = fullName;
    this.pricingPlan = pricingPlan;
    this.availableModels = availableModels;
    this.toolWindowModels = toolWindowModels;
    this.avatarBase64 = avatarBase64;
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

  public List<AvailableModel> getToolWindowModels() {
    return toolWindowModels;
  }

  public String getAvatarBase64() {
    return avatarBase64;
  }
}