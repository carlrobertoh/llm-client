package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.llm.client.you.YouClient;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequest;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequestMessage;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class YouClientTest extends BaseTest {

  @Test
  void shouldStreamYouChatCompletion() {
    var resultMessageBuilder = new StringBuilder();
    var chatId = UUID.randomUUID();
    var queryTraceId = UUID.randomUUID();
    expectStreamRequest("/api/streamingSearch", request -> {
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getUri().getPath()).isEqualTo("/api/streamingSearch");
      assertThat(request.getUri().getQuery()).isEqualTo(
          "q=TEST_PROMPT&" +
              "page=1&" +
              "count=10&" +
              "safeSearch=WebPages,Translations,TimeZone,Computation,RelatedSearches&" +
              "domain=youchat&" +
              "queryTraceId=" + queryTraceId + "&" +
              "chat=[{\"question\":\"Ping\",\"answer\":\"Pong\"}]&" +
              "chatId=" + chatId);
      assertThat(request.getHeaders())
          .flatExtracting("Host", "Accept", "Connection", "Cookie")
          .containsExactly("localhost:8000",
              "text/event-stream",
              "Keep-Alive",
              "uuid_guest=f9e7e074-54e1-43d9-a12d-30900b066d0c; " +
                  "safesearch_guest=Moderate; " +
                  "youpro_subscription=true; " +
                  "you_subscription=free; " +
                  "stytch_session=TEST_SESSION_ID; " +
                  "ydc_stytch_session=TEST_SESSION_ID; " +
                  "stytch_session_jwt=TEST_ACCESS_TOKEN; " +
                  "ydc_stytch_session_jwt=TEST_ACCESS_TOKEN; " +
                  "safesearch_9015f218b47611b62bbbaf61125cd2dac629e65c3d6f47573a2ec0e9b615c691=Moderate; " +
                  "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-AWIt5Mr4Y3xQI4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8SVMvBZPI3zkOgcRs7Mz3yazd7f7c58HwW5Xg9jdBjNg;");
      return List.of(
          jsonMapResponse("youChatToken", "Hel"),
          jsonMapResponse("youChatToken", "lo"),
          jsonMapResponse("youChatToken", "!"));
    });

    new YouClient.Builder("TEST_SESSION_ID", "TEST_ACCESS_TOKEN")
        .buildChatCompletionClient()
        .stream(
            new YouCompletionRequest.Builder("TEST_PROMPT")
                .setChatHistory(List.of(new YouCompletionRequestMessage("Ping", "Pong")))
                .setChatId(chatId)
                .setQueryTraceId(queryTraceId)
                .build(),
            new CompletionEventListener() {
              @Override
              public void onMessage(String message) {
                resultMessageBuilder.append(message);
              }
            });

    await().atMost(5, SECONDS).until(() -> "Hello!".contentEquals(resultMessageBuilder));
  }
}
