package ee.carlrobert.openai.client.billing;

import ee.carlrobert.openai.client.BaseClient;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.billing.response.CreditUsage;
import ee.carlrobert.openai.client.billing.response.Subscription;
import java.util.function.Consumer;
import okhttp3.Headers;
import okhttp3.Request;

public class BillingClient extends BaseClient {

  private static final String baseUrl = "https://api.openai.com/dashboard/billing";

  public BillingClient(OpenAIClient client) {
    super(client);
  }

  @Override
  protected ClientCode getClientCode() {
    return ClientCode.BILLING;
  }

  public void getCreditUsageAsync(Consumer<CreditUsage> responseConsumer) {
    buildClient()
        .newCall(buildGetRequest(baseUrl + "/credit_grants"))
        .enqueue(new BillingResponseCallback<>(responseConsumer, CreditUsage.class));
  }

  public void getSubscriptionAsync(Consumer<Subscription> responseConsumer) {
    buildClient()
        .newCall(buildGetRequest(baseUrl + "/subscription"))
        .enqueue(new BillingResponseCallback<>(responseConsumer, Subscription.class));
  }

  private Request buildGetRequest(String url) {
    return new Request.Builder()
        .url(url)
        .headers(Headers.of(baseHeaders))
        .get()
        .build();
  }
}
