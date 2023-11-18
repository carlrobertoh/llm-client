package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.llama.LlamaClient;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LlamaClientTest extends BaseTest {

  @Test
  void shouldStreamLlamaCompletion() {
    var resultMessageBuilder = new StringBuilder();
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/completion");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("prompt", "stream", "n_predict")
          .containsExactly("TEST_PROMPT", true, 10);
      assertThat(request.getHeaders())
          .flatExtracting("Accept", "Connection")
          .containsExactly("text/event-stream", "Keep-Alive");
      return List.of(
          jsonMapResponse("content", "Hel"),
          jsonMapResponse("content", "lo"),
          jsonMapResponse("content", "!"));
    });

    new LlamaClient.Builder()
        .build()
        .getChatCompletion(
            new LlamaCompletionRequest.Builder("TEST_PROMPT")
                .setN_predict(10)
                .build(),
            new CompletionEventListener() {
              @Override
              public void onMessage(String message) {
                resultMessageBuilder.append(message);
              }
            });

    await().atMost(5, SECONDS).until(() -> "Hello!".contentEquals(resultMessageBuilder));
  }

  @Test
  void shouldGetLlamaCompletion() {
    expectLlama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/completion");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("prompt", "stream", "n_predict")
          .containsExactly("TEST_PROMPT", false, 10);
      return new ResponseEntity(jsonMapResponse("content", "Hello!"));
    });

    var response = new LlamaClient.Builder()
        .build()
        .getChatCompletion(new LlamaCompletionRequest.Builder("TEST_PROMPT")
            .setStream(false)
            .setN_predict(10)
            .build());

    assertThat(response.getContent()).isEqualTo("Hello!");
  }
}