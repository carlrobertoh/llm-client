package ee.carlrobert.openai.client.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.PropertiesLoader;
import ee.carlrobert.openai.client.Client;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.dashboard.response.Subscription;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.Headers;
import okhttp3.Request;

public class DashboardClient {

  private static final String BASE_URL = PropertiesLoader.getValue("openai.baseUrl");
  private final String baseUrl;
  private final Client client;

  public DashboardClient(OpenAIClient client) {
    this.client = client;
    this.baseUrl = client.getHost() == null ? BASE_URL : client.getHost();
  }

  public ClientCode getClientCode() {
    return ClientCode.DASHBOARD;
  }

  public void getSubscriptionAsync(Consumer<Subscription> responseConsumer) {
    client.buildHttpClient()
        .newCall(buildGetRequest(baseUrl + "/dashboard/billing/subscription"))
        .enqueue(new DashboardResponseCallback<>(responseConsumer, Subscription.class));
  }

  public Subscription getSubscription() throws IOException {
    try (var response = client.buildHttpClient()
        .newCall(buildGetRequest(baseUrl + "/dashboard/billing/subscription"))
        .execute()) {
      if (response.body() != null) {
        return new ObjectMapper().readValue(response.body().string(), Subscription.class);
      }
    }
    return null;
  }

  private Request buildGetRequest(String url) {
    return new Request.Builder()
        .url(url)
        .headers(Headers.of(Map.of("Authorization", "Bearer " + client.getApiKey())))
        .get()
        .build();
  }
}
