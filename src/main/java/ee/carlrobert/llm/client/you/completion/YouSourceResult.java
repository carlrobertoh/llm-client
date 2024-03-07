package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouSourceResult {
  private final String url;
  private final boolean done;
  private final boolean isError;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public YouSourceResult(
      @JsonProperty("url") String url,
      @JsonProperty("done") boolean done,
      @JsonProperty("isError") boolean isError) {
    this.url = url;
    this.done = done;
    this.isError = isError;
  }

  public boolean isValid() {
    return done && !isError;
  }

  public String toString() {
    try {
      URL parsedUrl = new URL(url);
      String hostName = parsedUrl.getHost();

      return "[" + hostName + "](" + parsedUrl.toString() + ")";
    } catch (Exception e) {
      return url;
    }
  }
}
