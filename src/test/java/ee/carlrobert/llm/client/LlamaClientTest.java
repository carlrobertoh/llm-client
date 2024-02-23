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
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest.Builder;
import ee.carlrobert.llm.client.llama.completion.LlamaInfillRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

public class LlamaClientTest extends BaseTest {

  @Test
  void shouldStreamLlamaCompletion() {
    var resultMessageBuilder = new StringBuilder();
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/completion");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("prompt", "stream", "n_predict", "temperature", "top_k", "top_p", "min_p",
              "repeat_penalty")
          .containsExactly("TEST_PROMPT", true, 10, 0.1, 40, 0.9, 0.05, 1.1);
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
        .getChatCompletionAsync(
            new LlamaCompletionRequest.Builder("TEST_PROMPT")
                .setN_predict(10)
                .build(),
            new CompletionEventListener<String>() {
              @Override
              public void onMessage(String message, EventSource eventSource) {
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
          .extracting("prompt", "stream", "n_predict", "temperature", "top_k", "top_p", "min_p",
              "repeat_penalty")
          .containsExactly("TEST_PROMPT", false, 10, 0.5, 40, 0.9, 0.05, 1.1);
      return new ResponseEntity(jsonMapResponse("content", "Hello!"));
    });

    var response = new LlamaClient.Builder()
        .build()
        .getChatCompletion(new LlamaCompletionRequest.Builder("TEST_PROMPT")
            .setStream(false)
            .setN_predict(10)
            .setTemperature(0.5)
            .setTop_k(40)
            .setTop_p(0.9)
            .setMin_p(0.05)
            .setRepeat_penalty(1.1)
            .build());

    assertThat(response.getContent()).isEqualTo("Hello!");
  }

  @Test
  void shouldGetLlamaInfill() {
    expectLlama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/infill");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("input_prefix", "input_suffix", "stream", "n_predict", "temperature", "top_k",
              "top_p", "min_p",
              "repeat_penalty", "stop")
          .containsExactly("PREFIX", "SUFFIX", false, 10, 0.5, 40, 0.9, 0.05, 1.1,
              List.of("  <EOT>", "<EOT>"));
      return new ResponseEntity(jsonMapResponse("content", "Hello!"));
    });

    var response = new LlamaClient.Builder()
        .build()
        .getInfill(new LlamaInfillRequest(new Builder("")
            .setStream(false)
            .setN_predict(10)
            .setTemperature(0.5)
            .setTop_k(40)
            .setTop_p(0.9)
            .setMin_p(0.05)
            .setRepeat_penalty(1.1)
            .setStop(List.of("  <EOT>", "<EOT>")), "PREFIX", "SUFFIX")
        );

    assertThat(response.getContent()).isEqualTo("Hello!");
  }

  @Test
  void shouldStreamLlamaInfill() {
    var resultMessageBuilder = new StringBuilder();
    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/infill");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("input_prefix", "input_suffix", "stream", "n_predict", "stop")
          .containsExactly("TEST_PREFIX", "TEST_SUFFIX", true, 10, List.of("  <EOT>", "<EOT>"));
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
        .getInfillAsync(
            new LlamaInfillRequest(new Builder("")
                .setStream(true)
                .setN_predict(10)
                .setStop(List.of("  <EOT>", "<EOT>")),
                "TEST_PREFIX",
                "TEST_SUFFIX"),
            new CompletionEventListener<String>() {
              @Override
              public void onMessage(String message, EventSource eventSource) {
                resultMessageBuilder.append(message);
              }
            });

    await().atMost(5, SECONDS).until(() -> "Hello!".contentEquals(resultMessageBuilder));
  }
}