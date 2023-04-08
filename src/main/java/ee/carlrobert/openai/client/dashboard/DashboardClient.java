package ee.carlrobert.openai.client.dashboard;

import ee.carlrobert.openai.client.BaseClient;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.dashboard.response.Subscription;
import java.util.function.Consumer;
import okhttp3.Headers;
import okhttp3.Request;

public class DashboardClient extends BaseClient {

  private static final String baseUrl = "https://api.openai.com/dashboard/billing";

  public DashboardClient(OpenAIClient client) {
    super(client);
  }

  @Override
  protected ClientCode getClientCode() {
    return ClientCode.DASHBOARD;
  }

  public void getSubscriptionAsync(Consumer<Subscription> responseConsumer) {
    buildClient()
        .newCall(buildGetRequest(baseUrl + "/subscription"))
        .enqueue(new DashboardResponseCallback<>(responseConsumer, Subscription.class));
  }

  private Request buildGetRequest(String url) {
    return new Request.Builder()
        .url(url)
        .headers(Headers.of(baseHeaders))
        .get()
        .build();
  }
}
