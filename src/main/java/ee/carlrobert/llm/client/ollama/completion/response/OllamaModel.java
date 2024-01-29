package ee.carlrobert.llm.client.ollama.completion.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OllamaModel {

  private String name;
  private long size;
  private OllamaModelDetails details;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public OllamaModelDetails getDetails() {
    return details;
  }

  public void setDetails(OllamaModelDetails details) {
    this.details = details;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class OllamaModelDetails {

    private String format;
    private String family;
    private List<String> families;
    private String parameterSize;
    private String quantizationLevel;

    public String getFormat() {
      return format;
    }

    public void setFormat(String format) {
      this.format = format;
    }

    public String getFamily() {
      return family;
    }

    public void setFamily(String family) {
      this.family = family;
    }

    public List<String> getFamilies() {
      return families;
    }

    public void setFamilies(List<String> families) {
      this.families = families;
    }

    public String getParameterSize() {
      return parameterSize;
    }

    public void setParameterSize(String parameterSize) {
      this.parameterSize = parameterSize;
    }

    public String getQuantizationLevel() {
      return quantizationLevel;
    }

    public void setQuantizationLevel(String quantizationLevel) {
      this.quantizationLevel = quantizationLevel;
    }
  }
}
