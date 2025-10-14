package ee.carlrobert.llm.client.openai.response.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponseCompletionResponse {

  private String id;
  private String object;
  @JsonProperty("created_at")
  private Long created;
  private String model;
  @JsonProperty("service_tier")
  private String serviceTier;
  private List<OutputItem> output;
  private Usage usage;
  private Map<String, String> metadata;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public Long getCreated() {
    return created;
  }

  public void setCreated(Long created) {
    this.created = created;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getServiceTier() {
    return serviceTier;
  }

  public void setServiceTier(String serviceTier) {
    this.serviceTier = serviceTier;
  }

  public List<OutputItem> getOutput() {
    return output;
  }

  public void setOutput(List<OutputItem> output) {
    this.output = output;
  }

  public Usage getUsage() {
    return usage;
  }

  public void setUsage(Usage usage) {
    this.usage = usage;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OutputItem {
    private String type;
    private String role;
    private Object content;
    private String status;
    @JsonProperty("status_details")
    private Object statusDetails;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public Object getContent() {
      return content;
    }

    public void setContent(Object content) {
      this.content = content;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public Object getStatusDetails() {
      return statusDetails;
    }

    public void setStatusDetails(Object statusDetails) {
      this.statusDetails = statusDetails;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Usage {
    @JsonProperty("input_tokens")
    private Integer inputTokens;
    @JsonProperty("output_tokens")
    private Integer outputTokens;
    @JsonProperty("total_tokens")
    private Integer totalTokens;
    @JsonProperty("input_token_details")
    private TokenDetails inputTokenDetails;
    @JsonProperty("output_token_details")
    private TokenDetails outputTokenDetails;

    public Integer getInputTokens() {
      return inputTokens;
    }

    public void setInputTokens(Integer inputTokens) {
      this.inputTokens = inputTokens;
    }

    public Integer getOutputTokens() {
      return outputTokens;
    }

    public void setOutputTokens(Integer outputTokens) {
      this.outputTokens = outputTokens;
    }

    public Integer getTotalTokens() {
      return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
      this.totalTokens = totalTokens;
    }

    public TokenDetails getInputTokenDetails() {
      return inputTokenDetails;
    }

    public void setInputTokenDetails(TokenDetails inputTokenDetails) {
      this.inputTokenDetails = inputTokenDetails;
    }

    public TokenDetails getOutputTokenDetails() {
      return outputTokenDetails;
    }

    public void setOutputTokenDetails(TokenDetails outputTokenDetails) {
      this.outputTokenDetails = outputTokenDetails;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenDetails {
      @JsonProperty("cached_tokens")
      private Integer cachedTokens;
      @JsonProperty("reasoning_tokens")
      private Integer reasoningTokens;

      public Integer getCachedTokens() {
        return cachedTokens;
      }

      public void setCachedTokens(Integer cachedTokens) {
        this.cachedTokens = cachedTokens;
      }

      public Integer getReasoningTokens() {
        return reasoningTokens;
      }

      public void setReasoningTokens(Integer reasoningTokens) {
        this.reasoningTokens = reasoningTokens;
      }
    }
  }
}
