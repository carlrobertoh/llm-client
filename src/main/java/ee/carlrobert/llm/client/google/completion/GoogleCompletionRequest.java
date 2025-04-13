package ee.carlrobert.llm.client.google.completion;

import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * See <a href="https://ai.google.dev/api/generate-content?hl=en&authuser=1#request-body">Request-Body</a>.
 */
public class GoogleCompletionRequest implements CompletionRequest {

  private final List<GoogleCompletionContent> contents;
  private final List<SafetySetting> safetySettings;
  private final GoogleGenerationConfig generationConfig;
  private final GoogleCompletionContent systemInstruction;

  public GoogleCompletionRequest(Builder builder) {
    this.contents = builder.contents;
    this.safetySettings = builder.safetySettings;
    this.generationConfig = builder.generationConfig;
    this.systemInstruction = builder.systemInstruction;
  }

  public List<GoogleCompletionContent> getContents() {
    return contents;
  }

  public List<SafetySetting> getSafetySettings() {
    return safetySettings;
  }

  public GoogleGenerationConfig getGenerationConfig() {
    return generationConfig;
  }

  public GoogleCompletionContent getSystemInstruction() {
    return systemInstruction;
  }

  /**
   * <a href="https://ai.google.dev/api/rest/v1/HarmCategory?authuser=1">Harm Category</a>.
   */
  public enum HarmCategory {
    HARM_CATEGORY_UNSPECIFIED,
    HARM_CATEGORY_DEROGATORY,
    HARM_CATEGORY_TOXICITY,
    HARM_CATEGORY_VIOLENCE,
    HARM_CATEGORY_SEXUAL,
    HARM_CATEGORY_MEDICAL,
    HARM_CATEGORY_DANGEROUS,
    HARM_CATEGORY_HARASSMENT,
    HARM_CATEGORY_HATE_SPEECH,
    HARM_CATEGORY_SEXUALLY_EXPLICIT,
    HARM_CATEGORY_DANGEROUS_CONTENT;
  }

  /**
   * <a href="https://ai.google.dev/api/rest/v1/SafetySetting?authuser=1#harmblockthreshold">Harm
   * Block Threshold</a>.
   */
  public enum HarmBlockThreshold {
    HARM_BLOCK_THRESHOLD_UNSPECIFIED,
    BLOCK_LOW_AND_ABOVE,
    BLOCK_MEDIUM_AND_ABOVE,
    BLOCK_ONLY_HIGH,
    BLOCK_NONE,
  }

  public static class SafetySetting {

    private final HarmCategory category;
    private final HarmBlockThreshold threshold;

    public SafetySetting(HarmCategory category, HarmBlockThreshold threshold) {
      this.category = category;
      this.threshold = threshold;
    }

    public HarmCategory getCategory() {
      return category;
    }

    public HarmBlockThreshold getThreshold() {
      return threshold;
    }
  }

  public static class Builder {

    private List<GoogleCompletionContent> contents;
    private List<SafetySetting> safetySettings = new ArrayList<>();
    private GoogleGenerationConfig generationConfig = new GoogleGenerationConfig.Builder().build();
    private GoogleCompletionContent systemInstruction = null;

    public Builder(List<GoogleCompletionContent> contents) {
      this.contents = contents;
    }

    public Builder safetySettings(List<SafetySetting> safetySettings) {
      this.safetySettings = safetySettings;
      return this;
    }

    public Builder generationConfig(GoogleGenerationConfig generationConfig) {
      this.generationConfig = generationConfig;
      return this;
    }

    public Builder systemInstruction(GoogleCompletionContent systemInstruction) {
      this.systemInstruction = systemInstruction;
      return this;
    }

    public Builder systemInstruction(String systemInstruction) {
      this.systemInstruction = new GoogleCompletionContent(List.of(systemInstruction));
      return this;
    }

    public GoogleCompletionRequest build() {
      return new GoogleCompletionRequest(this);
    }
  }
}