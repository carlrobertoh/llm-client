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
import ee.carlrobert.llm.client.azure.AzureClient;
import ee.carlrobert.llm.client.azure.AzureCompletionRequestParams;
import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.openai.completion.ChatCompletionResponseData;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.imagegen.request.OpenAIImageGenerationRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Map;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

class AzureClientTest extends BaseTest {

  @Test
  void shouldStreamAzureChatCompletion() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectAzure((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/openai/deployments/TEST_DEPLOYMENT_ID/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "temperature",
              "stream",
              "max_tokens",
              "frequency_penalty",
              "presence_penalty",
              "messages")
          .containsExactly(
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

    new AzureClient.Builder("TEST_API_KEY",
        new AzureCompletionRequestParams(
            "TEST_RESOURCE",
            "TEST_DEPLOYMENT_ID",
            "TEST_API_VERSION"))
        .setActiveDirectoryAuthentication(true)
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
                .build(),
            new CompletionEventListener<ChatCompletionResponseData>() {
              @Override
              public void onMessage(ChatCompletionResponseData message, EventSource eventSource) {
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
  void shouldGetAzureImageGeneration() {
    var prompt = "TEST_PROMPT";
    expectAzure((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/openai/deployments/dalle3/images/generations");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "n",
              "quality",
              "response_format",
              "style",
              "model")
          .containsExactly(
              2,
              OpenAIImageGenerationRequest.ImageQuality.STANDARD.getQuality(),
              OpenAIImageGenerationRequest.ImagesResponseFormat.URL.getResponseFormat(),
              OpenAIImageGenerationRequest.ImageStyle.VIVID.getStyle(),
              "dalle3");
      return new ResponseEntity(new ObjectMapper().writeValueAsString(
          Map.of("data", List.of(Map.of("url", "url-to-image",
              "revised_prompt", "revised-prompt-value")))));
    });

    var response = new AzureClient.Builder(
        "TEST_API_KEY",
        new AzureCompletionRequestParams(
            "TEST_RESOURCE",
            "TEST_DEPLOYMENT_ID",
            "TEST_API_VERSION"))
        .setActiveDirectoryAuthentication(true)
        .build()
        .getImage(new OpenAIImageGenerationRequest.Builder(prompt)
            .setNumberOfImages(2)
            .setModel("dalle3")
            .setQuality(OpenAIImageGenerationRequest.ImageQuality.STANDARD)
            .setResponseFormat(OpenAIImageGenerationRequest.ImagesResponseFormat.URL)
            .setStyle(OpenAIImageGenerationRequest.ImageStyle.VIVID)
            .build());

    assertThat(response)
        .extracting("data")
        .extracting(result -> ((List<?>) result).get(0))
        .extracting("url", "revisedPrompt")
        .containsExactly("url-to-image", "revised-prompt-value");
  }

  @Test
  void shouldGetAzureChatCompletion() {
    var prompt = "TEST_PROMPT";
    expectAzure((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/openai/deployments/TEST_DEPLOYMENT_ID/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "temperature",
              "max_tokens",
              "frequency_penalty",
              "presence_penalty",
              "messages")
          .containsExactly(
              0.5,
              500,
              0.1,
              0.1,
              List.of(Map.of("role", "user", "content", prompt)));
      return new ResponseEntity(new ObjectMapper().writeValueAsString(
          Map.of("choices", List.of(Map.of("message", Map.of(
              "role", "assistant",
              "content", "This is a test"))))));
    });

    var response = new AzureClient.Builder(
        "TEST_API_KEY",
        new AzureCompletionRequestParams(
                "TEST_RESOURCE",
                "TEST_DEPLOYMENT_ID",
                "TEST_API_VERSION"))
        .setActiveDirectoryAuthentication(true)
        .build()
        .getChatCompletion(new OpenAIChatCompletionRequest.Builder(
            List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
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
  void shouldStreamAzureChatCompletionWithCustomURL() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectAzure((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/deployments/TEST_DEPLOYMENT_ID/completions");
      assertThat(request.getUri().getQuery()).isEqualTo("api_version=TEST_API_VERSION");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "temperature",
              "stream",
              "max_tokens",
              "frequency_penalty",
              "presence_penalty",
              "messages")
          .containsExactly(
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

    new AzureClient.Builder("TEST_API_KEY",
        new AzureCompletionRequestParams(
            "TEST_RESOURCE",
            "TEST_DEPLOYMENT_ID",
            "TEST_API_VERSION"))
        .setActiveDirectoryAuthentication(true)
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setPresencePenalty(0.1)
                .setFrequencyPenalty(0.1)
                .setOverriddenPath("/v1/deployments/%s/completions?api_version=%s")
                .build(),
            new CompletionEventListener<ChatCompletionResponseData>() {
              @Override
              public void onMessage(ChatCompletionResponseData message, EventSource eventSource) {
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
  void shouldListenForInvalidTokenErrorResponse() {
    var errorMessageBuilder = new StringBuilder();
    var errorResponse = jsonMapResponse(
        e("statusCode", 401),
        e("message", "Token is invalid"));
    expectAzure((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/openai/deployments/TEST_DEPLOYMENT_ID/chat/completions");
      return new ResponseEntity(401, errorResponse);
    });

    new AzureClient.Builder("TEST_API_KEY",
        new AzureCompletionRequestParams(
            "TEST_RESOURCE",
            "TEST_DEPLOYMENT_ID",
            "TEST_API_VERSION"))
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")))
                .build(),
            new CompletionEventListener<ChatCompletionResponseData>() {
              @Override
              public void onError(ErrorDetails error, Throwable t) {
                errorMessageBuilder.append(error.getMessage());
              }
            });

    await().atMost(5, SECONDS).until(() -> "Token is invalid".contentEquals(errorMessageBuilder));
  }

  @Test
  void shouldListenForInvalidResourceErrorResponse() {
    var errorMessageBuilder = new StringBuilder();
    var errorResponse = jsonMapResponse("error", jsonMap(
        e("message", "Resource not found"),
        e("code", "404")));
    expectAzure((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/openai/deployments/TEST_DEPLOYMENT_ID/chat/completions");
      return new ResponseEntity(404, errorResponse);
    });

    new AzureClient.Builder("TEST_API_KEY",
        new AzureCompletionRequestParams(
            "TEST_RESOURCE",
            "TEST_DEPLOYMENT_ID",
            "TEST_API_VERSION"))
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")))
                .build(),
            new CompletionEventListener<ChatCompletionResponseData>() {
              @Override
              public void onError(ErrorDetails error, Throwable t) {
                errorMessageBuilder.append(error.getMessage());
              }
            });

    await().atMost(5, SECONDS).until(() -> "Resource not found".contentEquals(errorMessageBuilder));
  }
}
