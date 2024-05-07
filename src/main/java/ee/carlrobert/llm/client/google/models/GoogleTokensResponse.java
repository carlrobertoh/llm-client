package ee.carlrobert.llm.client.google.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleTokensResponse {

  private int totalTokens;

  public int getTotalTokens() {
    return totalTokens;
  }

  public void setTotalTokens(int totalTokens) {
    this.totalTokens = totalTokens;
  }
}
