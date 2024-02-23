package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.you.UTMParameters;
import ee.carlrobert.llm.client.you.YouClient;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequest;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequestMessage;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.UUID;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

public class YouClientTest extends BaseTest {

  @Test
  void shouldStreamYouChatCompletion() {
    var utmParameters = new UTMParameters();
    utmParameters.setId("TEST_ID");
    utmParameters.setSource("TEST_SOURCE");
    utmParameters.setMedium("TEST_MEDIUM");
    utmParameters.setCampaign("TEST_CAMPAIGN");
    utmParameters.setContent("TEST_CONTENT");
    utmParameters.setTerm("TEST_TERM");
    var userId = UUID.randomUUID();
    var chatId = UUID.randomUUID();
    var queryTraceId = UUID.randomUUID();
    expectYou((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/streamingSearch");
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getUri().getQuery()).isEqualTo(
          "q=TEST_PROMPT&"
              + "page=1&"
              + "cfr=CodeGPT&"
              + "count=10&"
              + "safeSearch=WebPages,Translations,TimeZone,Computation,RelatedSearches&"
              + "domain=youchat&"
              + "chat=[{\"question\":\"Ping\",\"answer\":\"Pong\"}]&"
              + "chatId=" + chatId + "&"
              + "queryTraceId=" + queryTraceId + "&"
              + "utm_id=TEST_ID&"
              + "utm_source=TEST_SOURCE&"
              + "utm_medium=TEST_MEDIUM&"
              + "utm_campaign=TEST_CAMPAIGN&"
              + "utm_content=TEST_CONTENT&"
              + "utm_term=TEST_TERM");
      assertThat(request.getHeaders())
          .flatExtracting("Accept", "Connection", "User-agent", "Cookie")
          .containsExactly(
              "text/event-stream",
              "Keep-Alive",
              "youide CodeGPT",
              "uuid_guest=" + userId + "; "
                  + "safesearch_guest=Moderate; "
                  + "youpro_subscription=true; "
                  + "you_subscription=free; "
                  + "stytch_session=TEST_SESSION_ID; "
                  + "ydc_stytch_session=TEST_SESSION_ID; "
                  + "stytch_session_jwt=TEST_ACCESS_TOKEN; "
                  + "ydc_stytch_session_jwt=TEST_ACCESS_TOKEN; "
                  + "eg4=true; "
                  + "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-AWIt5Mr4Y3xQI"
                  + "4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8SVMvBZPI3zkOgcRs7Mz3yazd7f"
                  + "7c58HwW5Xg9jdBjNg;");
      return List.of(
          jsonMapResponse("youChatToken", "Hel"),
          jsonMapResponse("youChatToken", "lo"),
          jsonMapResponse("youChatToken", "!"));
    });
    var resultMessageBuilder = new StringBuilder();

    new YouClient.Builder("TEST_SESSION_ID", "TEST_ACCESS_TOKEN")
        .setUTMParameters(utmParameters)
        .build()
        .getChatCompletionAsync(
            new YouCompletionRequest.Builder("TEST_PROMPT")
                .setChatHistory(List.of(new YouCompletionRequestMessage("Ping", "Pong")))
                .setChatId(chatId)
                .setUserId(userId)
                .setUseGPT4Model(true)
                .setQueryTraceId(queryTraceId)
                .build(),
            new CompletionEventListener<String>() {
              @Override
              public void onMessage(String message, EventSource eventSource) {
                resultMessageBuilder.append(message);
              }
            });

    await().atMost(5, SECONDS).until(() -> "Hello!".contentEquals(resultMessageBuilder));
  }
}
