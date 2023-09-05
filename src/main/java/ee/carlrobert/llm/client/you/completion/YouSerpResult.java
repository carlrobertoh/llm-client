package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouSerpResult {

  private final String url;
  private final String name;
  private final String snippet;
  private final String snippetSource;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public YouSerpResult(
      @JsonProperty("url") String url,
      @JsonProperty("name") String name,
      @JsonProperty("snippet") String snippet,
      @JsonProperty("snippet_source") String snippetSource) {
    this.url = url;
    this.name = name;
    this.snippet = snippet;
    this.snippetSource = snippetSource;
  }

  public String getUrl() {
    return url;
  }

  public String getName() {
    return name;
  }

  public String getSnippet() {
    return snippet;
  }

  public String getSnippetSource() {
    return snippetSource;
  }
}
