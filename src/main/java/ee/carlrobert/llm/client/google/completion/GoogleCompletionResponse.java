package ee.carlrobert.llm.client.google.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionRequest.HarmCategory;
import ee.carlrobert.llm.completion.CompletionResponse;
import java.util.List;

/**
 * <a
 * href="https://ai.google.dev/api/rest/v1/GenerateContentResponse?authuser=1">GenerateContentResponse</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleCompletionResponse implements CompletionResponse {

  private List<Candidate> candidates;
  private PromptFeedback promptFeedback;

  public List<Candidate> getCandidates() {
    return candidates;
  }

  public void setCandidates(
      List<Candidate> candidates) {
    this.candidates = candidates;
  }

  public PromptFeedback getPromptFeedback() {
    return promptFeedback;
  }

  public void setPromptFeedback(
      PromptFeedback promptFeedback) {
    this.promptFeedback = promptFeedback;
  }

  public static class CitationSource {

    private int startIndex;
    private int endIndex;
    private String uri;
    private String license;

    public int getStartIndex() {
      return startIndex;
    }

    public void setStartIndex(int startIndex) {
      this.startIndex = startIndex;
    }

    public int getEndIndex() {
      return endIndex;
    }

    public void setEndIndex(int endIndex) {
      this.endIndex = endIndex;
    }

    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }

    public String getLicense() {
      return license;
    }

    public void setLicense(String license) {
      this.license = license;
    }
  }

  public static class CitationMetadata {

    private List<CitationSource> citationSources;

    public List<CitationSource> getCitationSources() {
      return citationSources;
    }

    public void setCitationSources(List<CitationSource> citationSources) {
      this.citationSources = citationSources;
    }
  }

  /**
   * <a
   * href="https://ai.google.dev/api/rest/v1/GenerateContentResponse?authuser=1#FinishReason">FinishReason</a>.
   */
  public enum FinishReason {
    FINISH_REASON_UNSPECIFIED,
    STOP,
    MAX_TOKENS,
    SAFETY,
    RECITATION,
    OTHER
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Candidate {

    private GoogleCompletionContent content;
    private FinishReason finishReason;
    private List<SafetyRating> safetyRatings;
    private CitationMetadata citationMetaData;
    private int tokenCount;
    private int index;

    public GoogleCompletionContent getContent() {
      return content;
    }

    public void setContent(GoogleCompletionContent content) {
      this.content = content;
    }

    public FinishReason getFinishReason() {
      return finishReason;
    }

    public void setFinishReason(
        FinishReason finishReason) {
      this.finishReason = finishReason;
    }

    public List<SafetyRating> getSafetyRatings() {
      return safetyRatings;
    }

    public void setSafetyRatings(
        List<SafetyRating> safetyRatings) {
      this.safetyRatings = safetyRatings;
    }

    public CitationMetadata getCitationMetaData() {
      return citationMetaData;
    }

    public void setCitationMetaData(
        CitationMetadata citationMetaData) {
      this.citationMetaData = citationMetaData;
    }

    public int getTokenCount() {
      return tokenCount;
    }

    public void setTokenCount(int tokenCount) {
      this.tokenCount = tokenCount;
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }
  }

  /**
   * <a
   * href="https://ai.google.dev/api/rest/v1/GenerateContentResponse?authuser=1#HarmProbability">HarmProbability</a>.
   */
  public enum HarmProbability {
    HARM_PROBABILITY_UNSPECIFIED,
    NEGLIGIBLE,
    LOW,
    MEDIUM,
    HIGH
  }

  public static class SafetyRating {

    private HarmCategory category;
    private HarmProbability probability;
    private boolean blocked;

    public HarmCategory getCategory() {
      return category;
    }

    public void setCategory(
        HarmCategory category) {
      this.category = category;
    }

    public HarmProbability getProbability() {
      return probability;
    }

    public void setProbability(HarmProbability probability) {
      this.probability = probability;
    }

    public boolean isBlocked() {
      return blocked;
    }

    public void setBlocked(boolean blocked) {
      this.blocked = blocked;
    }
  }

  /**
   * <a
   * href="https://ai.google.dev/api/rest/v1/GenerateContentResponse?authuser=1#BlockReason">BlockReason</a>.
   */
  public enum BlockReason {
    BLOCK_REASON_UNSPECIFIED,
    SAFETY,
    OTHER
  }

  public static class PromptFeedback {

    private BlockReason blockReason;
    private List<SafetyRating> safetyRatings;

    public BlockReason getBlockReason() {
      return blockReason;
    }

    public void setBlockReason(
        BlockReason blockReason) {
      this.blockReason = blockReason;
    }

    public List<SafetyRating> getSafetyRatings() {
      return safetyRatings;
    }

    public void setSafetyRatings(
        List<SafetyRating> safetyRatings) {
      this.safetyRatings = safetyRatings;
    }
  }
}