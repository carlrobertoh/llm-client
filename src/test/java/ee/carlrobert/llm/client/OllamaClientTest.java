package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.ollama.OllamaClient;
import ee.carlrobert.llm.client.ollama.completion.OllamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.OllamaCompletionRequestOptions;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class OllamaClientTest extends BaseTest {

  @Test
  void shouldStreamOllamaCompletion() {
    var options = new OllamaCompletionRequestOptions();
    options.setNumPredict(100);
    options.setTemperature(0.5);
    var completionRequest = new OllamaCompletionRequest("TEST_PROMPT", "TEST_MODEL");
    completionRequest.setSystem("TEST_SYSTEM_PROMPT");
    completionRequest.setStream(true);
    completionRequest.setOptions(options);
    var resultMessageBuilder = new StringBuilder();
    expectOllama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/generate");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("model", "prompt", "system", "stream", "options")
          .containsExactly(
              "TEST_MODEL",
              "TEST_PROMPT",
              "TEST_SYSTEM_PROMPT",
              true,
              Map.of(
                  "temperature", 0.5,
                  "num_predict", 100));
      assertThat(request.getHeaders())
          .flatExtracting("Accept", "Connection")
          .containsExactly("text/event-stream", "Keep-Alive");
      return List.of(
          jsonMapResponse("response", "Hel"),
          jsonMapResponse("response", "lo"),
          jsonMapResponse("response", "!"));
    });

    new OllamaClient.Builder()
        .build()
        .getChatCompletionAsync(
            completionRequest,
            new CompletionEventListener() {
              @Override
              public void onMessage(String message) {
                resultMessageBuilder.append(message);
              }
            });

    await().atMost(5, SECONDS).until(() -> "Hello!".contentEquals(resultMessageBuilder));
  }

  @Test
  void shouldGetOllamaCompletion() {
    expectOllama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/generate");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("model", "prompt", "stream")
          .containsExactly("TEST_MODEL", "TEST_PROMPT", false);
      return new ResponseEntity(jsonMapResponse("response", "Hello!"));
    });

    var response = new OllamaClient.Builder()
        .build()
        .getChatCompletion(new OllamaCompletionRequest("TEST_PROMPT", "TEST_MODEL"));

    assertThat(response.getResponse()).isEqualTo("Hello!");
  }
}
