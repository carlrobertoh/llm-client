package ee.carlrobert.openai;

import static ee.carlrobert.openai.util.JSONUtil.e;
import static ee.carlrobert.openai.util.JSONUtil.jsonArray;
import static ee.carlrobert.openai.util.JSONUtil.jsonMap;
import static ee.carlrobert.openai.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import ee.carlrobert.openai.http.ResponseEntity;
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
              "messages",
              "additionalParam1",
              "additionalParam2")
          .containsExactly(
              "gpt-3.5-turbo",
              0.5,
              true,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)),
              10,
              "testValue");
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hello")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .buildChatCompletionClient()
        .stream(
            (ChatCompletionRequest) new ChatCompletionRequest.Builder(
                List.of(new ChatCompletionMessage("user", prompt)))
                .setModel(ChatCompletionModel.GPT_3_5)
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
                .setAdditionalParams(Map.of(
                    "additionalParam1", 10,
                    "additionalParam2", "testValue"))
                .build(),
            new CompletionEventListener() {
              @Override
              public void onMessage(String message) {
                resultMessageBuilder.append(message);
              }

              @Override
              public void onComplete(StringBuilder messageBuilder) {
                assertThat(messageBuilder.toString()).isEqualTo(resultMessageBuilder.toString());
              }
            });

    await().atMost(5, SECONDS).until(() -> "Hello!".contentEquals(resultMessageBuilder));
  }

  @Test
  void shouldStreamTextCompletion() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectStreamRequest("/v1/completions", request -> {
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization")).isNull();
      assertThat(request.getBody())
          .extracting(
              "model",
              "prompt",
              "stop",
              "temperature",
              "stream",
              "max_tokens",
              "frequency_penalty",
              "presence_penalty")
          .containsExactly(
              "text-curie-001",
              prompt,
              List.of(" Human:", " AI:"),
              0.1,
              true,
              1000,
              0.2,
              0.2);
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("text", "He"))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "llo"))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "!"))));
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .buildTextCompletionClient()
        .stream(
            (TextCompletionRequest) new TextCompletionRequest.Builder(prompt)
                .setModel(TextCompletionModel.CURIE)
                .setStop(List.of(" Human:", " AI:"))
                .setMaxTokens(1000)
                .setTemperature(0.1)
                .setPresencePenalty(0.2)
                .setFrequencyPenalty(0.2)
                .build(),
            new CompletionEventListener() {
              @Override
              public void onMessage(String message) {
                resultMessageBuilder.append(message);
              }

              @Override
              public void onComplete(StringBuilder messageBuilder) {
                assertThat(messageBuilder.toString()).isEqualTo(resultMessageBuilder.toString());
              }
            });

    await().atMost(5, SECONDS).until(() -> "Hello!".contentEquals(resultMessageBuilder));
  }

  @Test
  void shouldFetchSubscriptionAsync() {
    StringBuilder accountName = new StringBuilder();
    expectRequest("/dashboard/billing/subscription", request -> {
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      return new ResponseEntity(jsonMapResponse("account_name", "TEST_ACCOUNT_NAME"));
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .buildDashboardClient()
        .getSubscriptionAsync(subscription -> {
          accountName.append(subscription.getAccountName());
        });

    await().atMost(5, SECONDS).until(() -> "TEST_ACCOUNT_NAME".contentEquals(accountName));
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
        .buildChatCompletionClient()
        .stream(
            new ChatCompletionRequest.Builder(
                List.of(new ChatCompletionMessage("user", "TEST_PROMPT")))
                .setModel(ChatCompletionModel.GPT_3_5)
                .build(),
            new CompletionEventListener() {
              @Override
              public void onError(ErrorDetails error) {
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
        .buildChatCompletionClient()
        .stream(
            new ChatCompletionRequest.Builder(
                List.of(new ChatCompletionMessage("user", "TEST_PROMPT")))
                .setModel(ChatCompletionModel.GPT_3_5)
                .build(),
            new CompletionEventListener() {
              @Override
              public void onError(ErrorDetails error) {
                errorMessageBuilder.append(error.getMessage());
              }
            });

    await().atMost(5, SECONDS)
        .until(() -> ("Unknown API response. "
            + "Code: 500, "
            + "Body: {\"error_details\":\"Server error\"}").contentEquals(errorMessageBuilder));
  }
}
