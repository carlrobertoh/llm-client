package ee.carlrobert.openai.client;

import java.util.Map;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

public abstract class BaseClient {

  protected final Map<String, String> baseHeaders;
  private final OpenAIClient client;

  protected abstract ClientCode getClientCode();

  public BaseClient(OpenAIClient client) {
    this.client = client;
    this.baseHeaders = Map.of(
        "Content-Type", "application/json",
        "Authorization", "Bearer " + client.apiKey);
  }

  protected OkHttpClient buildClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    if (client.connectTimeout != null && client.connectTimeoutUnit != null) {
      builder.connectTimeout(client.connectTimeout, client.connectTimeoutUnit);
    }
    if (client.readTimeout != null && client.readTimeoutUnit != null) {
      builder.readTimeout(client.readTimeout, client.readTimeoutUnit);
    }

    if (client.proxy != null) {
      builder.proxy(client.proxy);

      var authenticator = client.proxyAuthenticator;
      if (authenticator != null) {
        builder.proxyAuthenticator((route, response) ->
            response.request()
                .newBuilder()
                .header("Proxy-Authorization", Credentials.basic(authenticator.getUsername(), authenticator.getPassword()))
                .build());
      }
    }
    return builder.build();
  }
}
