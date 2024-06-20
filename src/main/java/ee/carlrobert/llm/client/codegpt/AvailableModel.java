package ee.carlrobert.llm.client.codegpt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailableModel {

  private final String name;
  private final String code;
  private final String type;
  private final String tier;
  private final String developedBy;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public AvailableModel(
      @JsonProperty("name") String name,
      @JsonProperty("code") String code,
      @JsonProperty("type") String type,
      @JsonProperty("tier") String tier,
      @JsonProperty("developedBy") String developedBy) {
    this.name = name;
    this.code = code;
    this.type = type;
    this.tier = tier;
    this.developedBy = developedBy;
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public String getType() {
    return type;
  }

  public String getTier() {
    return tier;
  }

  public String getDevelopedBy() {
    return developedBy;
  }
}