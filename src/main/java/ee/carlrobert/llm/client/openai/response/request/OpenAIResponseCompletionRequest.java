package ee.carlrobert.llm.client.openai.response.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAIResponseCompletionRequest {

  private Boolean background;
  private Object conversation;
  private List<String> include;
  private Object input;
  private String instructions;
  @JsonProperty("max_output_tokens")
  private Integer maxOutputTokens;
  @JsonProperty("max_tool_calls")
  private Integer maxToolCalls;
  private Map<String, String> metadata;
  private String model;
  @JsonProperty("parallel_tool_calls")
  private Boolean parallelToolCalls;
  @JsonProperty("previous_response_id")
  private String previousResponseId;
  private PromptTemplate prompt;
  @JsonProperty("prompt_cache_key")
  private String promptCacheKey;
  private ReasoningConfig reasoning;
  @JsonProperty("safety_identifier")
  private String safetyIdentifier;
  @JsonProperty("service_tier")
  private String serviceTier;
  private Boolean store;
  private Boolean stream;
  @JsonProperty("stream_options")
  private StreamOptions streamOptions;
  private Double temperature;
  private TextConfig text;
  @JsonProperty("tool_choice")
  private Object toolChoice;
  private List<Object> tools;
  @JsonProperty("top_logprobs")
  private Integer topLogprobs;
  @JsonProperty("top_p")
  private Double topP;
  private String truncation;

  public OpenAIResponseCompletionRequest() {
  }

  public static class Builder {
    private Boolean background;
    private Object conversation;
    private List<String> include;
    private Object input;
    private String instructions;
    private Integer maxOutputTokens;
    private Integer maxToolCalls;
    private Map<String, String> metadata;
    private String model;
    private Boolean parallelToolCalls;
    private String previousResponseId;
    private PromptTemplate prompt;
    private String promptCacheKey;
    private ReasoningConfig reasoning;
    private String safetyIdentifier;
    private String serviceTier;
    private Boolean store;
    private Boolean stream = true;
    private StreamOptions streamOptions;
    private Double temperature = 0.9;
    private TextConfig text;
    private Object toolChoice;
    private List<Object> tools;
    private Integer topLogprobs;
    private Double topP;
    private String truncation;

    public Builder() {
    }

    public Builder setBackground(Boolean background) {
      this.background = background;
      return this;
    }

    public Builder setConversation(Object conversation) {
      this.conversation = conversation;
      return this;
    }

    public Builder setInclude(List<String> include) {
      this.include = include;
      return this;
    }

    public Builder setInput(Object input) {
      this.input = input;
      return this;
    }

    public Builder setInstructions(String instructions) {
      this.instructions = instructions;
      return this;
    }

    public Builder setMaxOutputTokens(Integer maxOutputTokens) {
      this.maxOutputTokens = maxOutputTokens;
      return this;
    }

    public Builder setMaxToolCalls(Integer maxToolCalls) {
      this.maxToolCalls = maxToolCalls;
      return this;
    }

    public Builder setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
      return this;
    }

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setParallelToolCalls(Boolean parallelToolCalls) {
      this.parallelToolCalls = parallelToolCalls;
      return this;
    }

    public Builder setPreviousResponseId(String previousResponseId) {
      this.previousResponseId = previousResponseId;
      return this;
    }

    public Builder setPrompt(PromptTemplate prompt) {
      this.prompt = prompt;
      return this;
    }

    public Builder setPrompt(String id, Map<String, Object> variables) {
      var pt = new PromptTemplate();
      pt.setId(id);
      pt.setVariables(variables);
      this.prompt = pt;
      return this;
    }

    public Builder setPromptCacheKey(String promptCacheKey) {
      this.promptCacheKey = promptCacheKey;
      return this;
    }

    public Builder setReasoning(ReasoningConfig reasoning) {
      this.reasoning = reasoning;
      return this;
    }

    public Builder setSafetyIdentifier(String safetyIdentifier) {
      this.safetyIdentifier = safetyIdentifier;
      return this;
    }

    public Builder setServiceTier(String serviceTier) {
      this.serviceTier = serviceTier;
      return this;
    }

    public Builder setStore(Boolean store) {
      this.store = store;
      return this;
    }

    public Builder setStream(Boolean stream) {
      this.stream = stream;
      return this;
    }

    public Builder setStreamOptions(StreamOptions streamOptions) {
      this.streamOptions = streamOptions;
      return this;
    }

    public Builder setTemperature(Double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder setText(TextConfig text) {
      this.text = text;
      return this;
    }

    public Builder setToolChoice(Object toolChoice) {
      this.toolChoice = toolChoice;
      return this;
    }

    public Builder setTools(List<Object> tools) {
      this.tools = tools;
      return this;
    }

    public Builder setTopLogprobs(Integer topLogprobs) {
      this.topLogprobs = topLogprobs;
      return this;
    }

    public Builder setTopP(Double topP) {
      this.topP = topP;
      return this;
    }

    public Builder setTruncation(String truncation) {
      this.truncation = truncation;
      return this;
    }

    public OpenAIResponseCompletionRequest build() {
      var req = new OpenAIResponseCompletionRequest();
      req.setBackground(background);
      req.setConversation(conversation);
      req.setInclude(include);
      req.setInput(input);
      req.setInstructions(instructions);
      req.setMaxOutputTokens(maxOutputTokens);
      req.setMaxToolCalls(maxToolCalls);
      req.setMetadata(metadata);
      req.setModel(model);
      req.setParallelToolCalls(parallelToolCalls);
      req.setPreviousResponseId(previousResponseId);
      req.setPrompt(prompt);
      req.setPromptCacheKey(promptCacheKey);
      req.setReasoning(reasoning);
      req.setSafetyIdentifier(safetyIdentifier);
      req.setServiceTier(serviceTier);
      req.setStore(store);
      req.setStream(stream);
      req.setStreamOptions(streamOptions);
      req.setTemperature(temperature);
      req.setText(text);
      req.setToolChoice(toolChoice);
      req.setTools(tools);
      req.setTopLogprobs(topLogprobs);
      req.setTopP(topP);
      req.setTruncation(truncation);
      return req;
    }
  }

  public Boolean getBackground() {
    return background;
  }

  public void setBackground(Boolean background) {
    this.background = background;
  }

  public Object getConversation() {
    return conversation;
  }

  public void setConversation(Object conversation) {
    this.conversation = conversation;
  }

  public List<String> getInclude() {
    return include;
  }

  public void setInclude(List<String> include) {
    this.include = include;
  }

  public Object getInput() {
    return input;
  }

  public void setInput(Object input) {
    this.input = input;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public Integer getMaxOutputTokens() {
    return maxOutputTokens;
  }

  public void setMaxOutputTokens(Integer maxOutputTokens) {
    this.maxOutputTokens = maxOutputTokens;
  }

  public Integer getMaxToolCalls() {
    return maxToolCalls;
  }

  public void setMaxToolCalls(Integer maxToolCalls) {
    this.maxToolCalls = maxToolCalls;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Boolean getParallelToolCalls() {
    return parallelToolCalls;
  }

  public void setParallelToolCalls(Boolean parallelToolCalls) {
    this.parallelToolCalls = parallelToolCalls;
  }

  public String getPreviousResponseId() {
    return previousResponseId;
  }

  public void setPreviousResponseId(String previousResponseId) {
    this.previousResponseId = previousResponseId;
  }

  public PromptTemplate getPrompt() {
    return prompt;
  }

  public void setPrompt(PromptTemplate prompt) {
    this.prompt = prompt;
  }

  public String getPromptCacheKey() {
    return promptCacheKey;
  }

  public void setPromptCacheKey(String promptCacheKey) {
    this.promptCacheKey = promptCacheKey;
  }

  public ReasoningConfig getReasoning() {
    return reasoning;
  }

  public void setReasoning(ReasoningConfig reasoning) {
    this.reasoning = reasoning;
  }

  public String getSafetyIdentifier() {
    return safetyIdentifier;
  }

  public void setSafetyIdentifier(String safetyIdentifier) {
    this.safetyIdentifier = safetyIdentifier;
  }

  public String getServiceTier() {
    return serviceTier;
  }

  public void setServiceTier(String serviceTier) {
    this.serviceTier = serviceTier;
  }

  public Boolean getStore() {
    return store;
  }

  public void setStore(Boolean store) {
    this.store = store;
  }

  public Boolean getStream() {
    return stream;
  }

  public void setStream(Boolean stream) {
    this.stream = stream;
  }

  public StreamOptions getStreamOptions() {
    return streamOptions;
  }

  public void setStreamOptions(StreamOptions streamOptions) {
    this.streamOptions = streamOptions;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public TextConfig getText() {
    return text;
  }

  public void setText(TextConfig text) {
    this.text = text;
  }

  public Object getToolChoice() {
    return toolChoice;
  }

  public void setToolChoice(Object toolChoice) {
    this.toolChoice = toolChoice;
  }

  public List<Object> getTools() {
    return tools;
  }

  public void setTools(List<Object> tools) {
    this.tools = tools;
  }

  public Integer getTopLogprobs() {
    return topLogprobs;
  }

  public void setTopLogprobs(Integer topLogprobs) {
    this.topLogprobs = topLogprobs;
  }

  public Double getTopP() {
    return topP;
  }

  public void setTopP(Double topP) {
    this.topP = topP;
  }

  public String getTruncation() {
    return truncation;
  }

  public void setTruncation(String truncation) {
    this.truncation = truncation;
  }

  public boolean isStream() {
    return stream != null && stream;
  }

  public static class PromptTemplate {
    private String id;
    private Map<String, Object> variables;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Map<String, Object> getVariables() {
      return variables;
    }

    public void setVariables(Map<String, Object> variables) {
      this.variables = variables;
    }
  }

  public static class ReasoningConfig {
    @JsonProperty("max_reasoning_tokens")
    private Integer maxReasoningTokens;

    public Integer getMaxReasoningTokens() {
      return maxReasoningTokens;
    }

    public void setMaxReasoningTokens(Integer maxReasoningTokens) {
      this.maxReasoningTokens = maxReasoningTokens;
    }
  }

  public static class StreamOptions {
    @JsonProperty("include_usage")
    private Boolean includeUsage;

    public Boolean getIncludeUsage() {
      return includeUsage;
    }

    public void setIncludeUsage(Boolean includeUsage) {
      this.includeUsage = includeUsage;
    }
  }

  public static class TextConfig {
    private String type;
    private Object schema;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public Object getSchema() {
      return schema;
    }

    public void setSchema(Object schema) {
      this.schema = schema;
    }
  }
}
