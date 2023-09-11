package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OpenAIClientTest extends BaseTest {

  @Test
  void shouldStreamChatCompletion() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectStreamRequest("/v1/chat/completions", request -> {
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
              "gpt-3.5-turbo",
              0.5,
              true,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hello")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .build()
        .getChatCompletion(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionMessage("user", prompt)))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
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
  void shouldStreamChatCompletionWithCustomURL() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectStreamRequest("/v1/test/segment", request -> {
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
              "gpt-3.5-turbo",
              0.5,
              true,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hello")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    ((OpenAIClient) new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .setHost("http://127.0.0.1:8000")
        .build())
        .getChatCompletion(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionMessage("user", prompt)))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
                .setOverriddenPath("/v1/test/segment")
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
  void shouldGetChatCompletion() {
    var prompt = "TEST_PROMPT";
    expectRequest("/v1/chat/completions", request -> {
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
              "gpt-3.5-turbo",
              0.5,
              false,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)));

      return new ResponseEntity(new ObjectMapper().writeValueAsString
          (Map.of("choices", List.of(
              Map.of("message", Map.of(
                  "role", "assistant",
                  "content", "This is a test"))))));
    });

    var response = new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .build()
        .getChatCompletion(new OpenAIChatCompletionRequest.Builder(
            List.of(new OpenAIChatCompletionMessage("user", prompt)))
            .setModel(OpenAIChatCompletionModel.GPT_3_5)
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

  @Test
  void shouldHandleInvalidApiKeyError() {
    var errorMessageBuilder = new StringBuilder();
    var errorResponse = jsonMapResponse("error", jsonMap(
        e("message", "Incorrect API key provided"),
        e("type", "invalid_request_error"),
        e("code", "invalid_api_key")));
    expectRequest("/v1/chat/completions",
        request -> new ResponseEntity(401, errorResponse));

    new OpenAIClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletion(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionMessage("user", "TEST_PROMPT")))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
                .build(),
            new CompletionEventListener() {
              @Override
              public void onError(ErrorDetails error, Throwable t) {
                assertThat(error.getCode()).isEqualTo("invalid_api_key");
                assertThat(error.getType()).isEqualTo("invalid_request_error");
                errorMessageBuilder.append(error.getMessage());
              }
            });

    await().atMost(5, SECONDS)
        .until(() -> "Incorrect API key provided".contentEquals(errorMessageBuilder));
  }

  @Test
  void shouldHandleUnknownApiError() {
    var errorMessageBuilder = new StringBuilder();
    var errorResponse = jsonMapResponse("error_details", "Server error");
    expectRequest("/v1/chat/completions",
        request -> new ResponseEntity(500, errorResponse));

    new OpenAIClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletion(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionMessage("user", "TEST_PROMPT")))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
                .build(),
            new CompletionEventListener() {
              @Override
              public void onError(ErrorDetails error, Throwable t) {
                errorMessageBuilder.append(error.getMessage());
              }
            });

    await().atMost(5, SECONDS)
        .until(() -> ("Unknown API response. "
            + "Code: 500, "
            + "Body: {\"error_details\":\"Server error\"}").contentEquals(errorMessageBuilder));
  }

  @Test
  void shouldGetEmbeddings() {
    var embeddingResponse = new double[] {-0.00692, -0.0053, -4.5471, -0.0240};
    expectRequest("/v1/embeddings", request -> {
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      return new ResponseEntity(200, jsonMapResponse("data", jsonArray(jsonMap("embedding", embeddingResponse))));
    });

    var result = new OpenAIClient.Builder("TEST_API_KEY").build().getEmbedding("TEST_PROMPT");

    assertThat(result).isEqualTo(embeddingResponse);
  }
}
