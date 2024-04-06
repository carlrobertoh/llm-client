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
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.Tool;
import ee.carlrobert.llm.client.openai.completion.request.ToolFunction;
import ee.carlrobert.llm.client.openai.completion.request.ToolFunctionParameters;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Map;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

class OpenAIClientTest extends BaseTest {

  @Test
  void shouldStreamChatCompletion() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectOpenAI((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
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
  void shouldStreamCompletion() {
    var resultMessageBuilder = new StringBuilder();
    expectOpenAI((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
              "gpt-3.5-turbo-instruct",
              "TEST_PROMPT",
              "TEST_SUFFIX",
              0.5,
              true,
              500,
              0.1,
              0.1);
      return List.of(
          "{\"choices\": null}",
          "{\"choices\": [null]}",
          jsonMapResponse("choices", jsonArray()),
          jsonMapResponse("choices", jsonArray(jsonMap("text", null))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", ""))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", " "))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "Hello"))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "!"))));
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .build()
        .getCompletionAsync(new OpenAITextCompletionRequest.Builder("TEST_PROMPT")
                .setSuffix("TEST_SUFFIX")
                .setStream(true)
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
  void shouldStreamChatCompletionWithCustomURL() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectOpenAI((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/test/segment");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
              "gpt-3.5-turbo",
              0.5,
              true,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          "{\"choices\": null}",
          "{\"choices\": [null]}",
          jsonMapResponse("choices", jsonArray()),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", null))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", null)))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", " ")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hello")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
                .setOverriddenPath("/v1/test/segment")
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
    var prompt = "TEST_PROMPT";
    expectOpenAI((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
              "gpt-3.5-turbo",
              0.5,
              false,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)));

      return new ResponseEntity(new ObjectMapper().writeValueAsString(Map.of("choices", List.of(
          Map.of("message", Map.of(
              "role", "assistant",
              "content", "This is a test"))))));
    });

    var response = new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .build()
        .getChatCompletion(new OpenAIChatCompletionRequest.Builder(
            List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
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
  void shouldUseFunctionCalling() {
    var prompt = "TEST_PROMPT";
    expectOpenAI((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("Openai-organization").get(0))
          .isEqualTo("TEST_ORGANIZATION");
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
              "messages",
              "tools",
              "tool_choice")
          .containsExactly(
              "gpt-3.5-turbo",
              0.5,
              false,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)),
              List.of(Map.of("type", "function", "function",
                  Map.of(
                      "name", "get_current_weather",
                      "description", "Get the current weather in a given location",
                      "parameters", Map.of(
                          "type", "object",
                          "required", List.of("location"),
                          "properties", Map.of("location", Map.of(
                                  "type", "string",
                                  "description", "The city and state, e.g. San Francisco, CA"),
                              "unit", Map.of(
                                  "type", "string",
                                  "enum", List.of("celsius", "fahrenheit"))))))),
              "auto");

      return new ResponseEntity(
          new ObjectMapper().writeValueAsString(
              Map.of("choices", List.of(Map.of(
                  "finish_reason", "tool_calls",
                  "message", Map.of(
                      "role", "assistant",
                      "tool_calls", List.of(Map.of(
                          "id", "call_abc123",
                          "type", "function",
                          "function", Map.of(
                              "name", "get_current_weather",
                              "arguments", "{\n\"location\": \"Boston, MA\"\n}")))))))));
    });
    var parameters = new ToolFunctionParameters();
    parameters.setType("object");
    parameters.setProperties(Map.of(
        "location", Map.of(
            "type", "string",
            "description", "The city and state, e.g. San Francisco, CA"),
        "unit", Map.of(
            "type", "string",
            "enum", List.of("celsius", "fahrenheit"))
    ));
    parameters.setRequired(List.of("location"));
    var function = new ToolFunction();
    function.setName("get_current_weather");
    function.setDescription("Get the current weather in a given location");
    function.setParameters(parameters);
    var tool = new Tool();
    tool.setType("function");
    tool.setFunction(function);

    var response = new OpenAIClient.Builder("TEST_API_KEY")
        .setOrganization("TEST_ORGANIZATION")
        .build()
        .getChatCompletion(new OpenAIChatCompletionRequest.Builder(
            List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
            .setModel(OpenAIChatCompletionModel.GPT_3_5)
            .setMaxTokens(500)
            .setTemperature(0.5)
            .setPresencePenalty(0.1)
            .setFrequencyPenalty(0.1)
            .setTools(List.of(tool))
            .setToolChoice("auto")
            .setStream(false)
            .build());

    assertThat(response.getChoices()).hasSize(1);
    assertThat(response.getChoices().get(0)).isNotNull();
    var message = response.getChoices().get(0).getMessage();
    assertThat(message).isNotNull();
    assertThat(message.getRole()).isEqualTo("assistant");
    assertThat(message.getToolCalls()).hasSize(1);
    assertThat(message.getToolCalls().get(0).getId()).isEqualTo("call_abc123");
    assertThat(message.getToolCalls().get(0).getType()).isEqualTo("function");
    assertThat(message.getToolCalls().get(0).getFunction())
        .extracting("name", "arguments")
        .containsExactly("get_current_weather", "{\n\"location\": \"Boston, MA\"\n}");
  }

  @Test
  void shouldHandleInvalidApiKeyError() {
    var errorMessageBuilder = new StringBuilder();
    var errorResponse = jsonMapResponse("error", jsonMap(
        e("message", "Incorrect API key provided"),
        e("type", "invalid_request_error"),
        e("code", "invalid_api_key")));
    expectOpenAI((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      return new ResponseEntity(401, errorResponse);
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
                .build(),
            new CompletionEventListener<String>() {
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
    expectOpenAI((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      return new ResponseEntity(500, errorResponse);
    });

    new OpenAIClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")))
                .setModel(OpenAIChatCompletionModel.GPT_3_5)
                .build(),
            new CompletionEventListener<String>() {
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
    var embeddingResponse = new double[]{-0.00692, -0.0053, -4.5471, -0.0240};
    expectOpenAI((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/embeddings");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      return new ResponseEntity(200,
          jsonMapResponse("data", jsonArray(jsonMap("embedding", embeddingResponse))));
    });

    var result = new OpenAIClient.Builder("TEST_API_KEY")
        .build()
        .getEmbedding("TEST_PROMPT");

    assertThat(result).isEqualTo(embeddingResponse);
  }
}
