package ee.carlrobert.llm.client.you;

import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.you.completion.YouCompletionClient;

public class YouClient extends Client {

  private final String sessionId;
  private final String accessToken;

  private YouClient(YouClient.Builder builder) {
    super(builder);
    this.sessionId = builder.sessionId;
    this.accessToken = builder.accessToken;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public static class Builder extends Client.Builder {

    private final String sessionId;
    private final String accessToken;

    public Builder(String sessionId, String accessToken) {
      this.sessionId = sessionId;
      this.accessToken = accessToken;
    }

    public YouCompletionClient buildChatCompletionClient() {
      return new YouCompletionClient(new YouClient(this));
    }
  }
}
