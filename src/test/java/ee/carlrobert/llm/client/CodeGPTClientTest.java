package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.codegpt.CodeGPTClient;
import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.ResponseFormat;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Map;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

public class CodeGPTClientTest extends BaseTest {

  @Test
  void shouldStreamChatCompletion() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    var responseFormat = new ResponseFormat();
    responseFormat.setType("TEST_TYPE");
    expectCodeGPT((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "model",
              "temperature",
              "stream",
              "max_tokens",
              "frequency_penalty",
              "presence_penalty",
              "response_format",
              "messages")
          .containsExactly(
              "TEST_MODEL",
              0.5,
              true,
              500,
              0.1,
              0.1,
              Map.of("type", responseFormat.getType()),
              List.of(Map.of("role", "user", "content", prompt)));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hello")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    new CodeGPTClient("TEST_API_KEY")
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
                .setModel("TEST_MODEL")
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
                .setResponseFormat(responseFormat)
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
  void shouldStreamCompletion() {
    var resultMessageBuilder = new StringBuilder();
    expectCodeGPT((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "model",
              "prompt",
              "suffix",
              "temperature",
              "stream",
              "max_tokens",
              "frequency_penalty",
              "presence_penalty")
          .containsExactly(
              "TEST_MODEL",
              "TEST_PROMPT",
              "TEST_SUFFIX",
              0.5,
              true,
              500,
              0.1,
              0.1);
      return List.of(
          "{}",
          jsonMapResponse("choices", null),
          jsonMapResponse("choices", jsonArray()),
          jsonMapResponse("choices", jsonArray((Map<String, ?>) null)),
          jsonMapResponse("choices", jsonArray(jsonMap())),
          jsonMapResponse("choices", jsonArray(jsonMap("text", null))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", ""))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "Hello"))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "!"))));
    });

    new CodeGPTClient("TEST_API_KEY")
        .getCompletionAsync(new OpenAITextCompletionRequest.Builder("TEST_PROMPT")
                .setSuffix("TEST_SUFFIX")
                .setStream(true)
                .setModel("TEST_MODEL")
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
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
  void shouldGetChatCompletion() {
    expectCodeGPT((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "model",
              "temperature",
              "stream",
              "max_tokens",
              "frequency_penalty",
              "presence_penalty",
              "messages")
          .containsExactly(
              "TEST_MODEL",
              0.5,
              false,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", "TEST_PROMPT")));

      return new ResponseEntity(new ObjectMapper().writeValueAsString(Map.of("choices", List.of(
          Map.of("message", Map.of(
              "role", "assistant",
              "content", "This is a test"))))));
    });

    var response = new CodeGPTClient("TEST_API_KEY")
        .getChatCompletion(new OpenAIChatCompletionRequest.Builder(
            List.of(new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")))
            .setModel("TEST_MODEL")
            .setMaxTokens(500)
            .setTemperature(0.5)
            .setPresencePenalty(0.1)
            .setFrequencyPenalty(0.1)
            .setStream(false)
            .build());

    assertThat(response.getChoices())
        .extracting("message")
        .extracting("role", "content")
        .containsExactly(tuple("assistant", "This is a test"));
  }
}
