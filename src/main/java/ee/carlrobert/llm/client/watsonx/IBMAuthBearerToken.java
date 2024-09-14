package ee.carlrobert.llm.client.watsonx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IBMAuthBearerToken {

  @JsonProperty("access_token")
  String accessToken;
  @JsonProperty("expiration")
  int expiration;

  String getAccessToken() {
    return this.accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  int getExpiration() {
    return this.expiration;
  }

  public void setExpiration(int expiration) {
    this.expiration = expiration;
  }
}