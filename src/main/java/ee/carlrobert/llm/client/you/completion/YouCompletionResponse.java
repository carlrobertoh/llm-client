package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.completion.CompletionResponse;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouCompletionResponse implements CompletionResponse {

  private final String chatToken;
  private final String altChatToken;
  private final String section;
  private final boolean sectionDone;
  private final YouSourceResult sourceResult;

  private final List<YouSerpResult> serpResults;
  private final List<String> relatedSearches;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public YouCompletionResponse(
      @JsonProperty("youChatToken") String chatToken,
      @JsonProperty("youChatSerpResults") List<YouSerpResult> serpResults,
      @JsonProperty("t") String altChatToken,
      @JsonProperty("msg") String section,
      @JsonProperty("done") boolean sectionDone,
      @JsonProperty("relatedSearches") List<String> relatedSearches,
      @JsonProperty("search") YouThirdPartySearchResult thirdPartySearchResult,
      @JsonProperty("source") YouSourceResult sourceResult) {
    this.chatToken = chatToken;
    this.altChatToken = altChatToken;
    this.section = section;
    this.sectionDone = sectionDone;
    this.relatedSearches = relatedSearches;
    this.sourceResult = sourceResult;

    if (serpResults != null && !serpResults.isEmpty()) {
      this.serpResults = serpResults;
    } else if (thirdPartySearchResult != null && thirdPartySearchResult.hasSearchResults()) {
      this.serpResults = thirdPartySearchResult.getThirdPartySearchResults();
    } else {
      this.serpResults = null;
    }
  }

  public String getChatToken() {
    if (chatToken != null) {
      return chatToken;
    }

    if (altChatToken != null) {
      return altChatToken;
    }

    if (section != null && !sectionDone) {
      return "\n**" + section + "**\n\n";
    }

    if (relatedSearches != null) {
      return "\n**Related searches**\n\n" + relatedSearches.stream()
          .map(element -> "* " + element + "\n")
          .collect(Collectors.joining());
    }

    if (sourceResult != null && sourceResult.isValid()) {
      return "* " + sourceResult.toString() + "\n";
    }

    return null;
  }

  public List<YouSerpResult> getSerpResults() {
    return serpResults;
  }


}
