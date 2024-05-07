package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.google.GoogleClient;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionContent;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionRequest;
import ee.carlrobert.llm.client.google.completion.GoogleGenerationConfig;
import ee.carlrobert.llm.client.google.embedding.GoogleEmbeddingContentRequest;
import ee.carlrobert.llm.client.google.models.GoogleModel;
import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import okhttp3.sse.EventSource;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class GoogleClientTest extends BaseTest {

  @Test
  void shouldStreamChatCompletion() {
    var prompt = "TEST_PROMPT";
    var resultMessageBuilder = new StringBuilder();
    expectGoogle((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/gemini-1.0-pro:streamGenerateContent");
      assertThat(request.getUri().getQuery()).isEqualTo("key=TEST_API_KEY&alt=sse");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting(
              "generationConfig.temperature",
              "generationConfig.maxOutputTokens",
              "contents"
          )
          .containsExactly(
              0.5,
              500,
              List.of(Map.of("parts", List.of(Map.of("text", prompt)), "role", "user"))
          );
      return List.of(
          jsonMapResponse("candidates",
              jsonArray(jsonMap("content", jsonMap("parts", jsonArray(jsonMap("text", "Hello")))))),
          jsonMapResponse("candidates",
              jsonArray(jsonMap("content", jsonMap("parts", jsonArray(jsonMap("text", "!")))))));
    });

    new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletionAsync(
            new GoogleCompletionRequest.Builder(
                List.of(new GoogleCompletionContent("user", List.of(prompt))))
                .generationConfig(new GoogleGenerationConfig.Builder()
                    .temperature(0.5)
                    .maxOutputTokens(500)
                    .build())
                .build(),
            GoogleModel.GEMINI_1_0_PRO,
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
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/gemini-1.0-pro:generateContent");
      assertThat(request.getUri().getQuery()).isEqualTo("key=TEST_API_KEY");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting(
              "generationConfig.temperature",
              "generationConfig.maxOutputTokens",
              "contents"
          )
          .containsExactly(
              0.5,
              500,
              List.of(Map.of("parts", List.of(Map.of("text", prompt)), "role", "user"))
          );
      return new ResponseEntity(new ObjectMapper().writeValueAsString(Map.of("candidates", List.of(
          Map.of("content", Map.of(
              "role", "assistant",
              "parts", List.of(Map.of("text", "This is a test"))))))));
    });

    var response = new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletion(new GoogleCompletionRequest.Builder(
                List.of(new GoogleCompletionContent("user", List.of(prompt))))
                .generationConfig(new GoogleGenerationConfig.Builder()
                    .maxOutputTokens(500)
                    .temperature(0.5)
                    .build())
                .build(),
            GoogleModel.GEMINI_1_0_PRO);

    assertThat(response.getCandidates())
        .extracting("content.role")
        .containsExactly("assistant");

    assertThat(response.getCandidates())
        .first()
        .extracting("content.parts")
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .first()
        .extracting("text")
        .isEqualTo("This is a test");
  }


  @Test
  void shouldHandleInvalidApiKeyError() {
    var errorMessageBuilder = new StringBuilder();
    var errorResponse = jsonMapResponse("error", jsonMap(
        e("message", "API key not valid. Please pass a valid API key."),
        e("status", "INVALID_ARGUMENT"),
        e("code", "400")));
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/gemini-1.0-pro:streamGenerateContent");
      return new ResponseEntity(400, errorResponse);
    });

    new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletionAsync(
            new GoogleCompletionRequest.Builder(
                List.of(new GoogleCompletionContent("user", List.of("TEST_USER_PROMPT"))))
                .build(),
            GoogleModel.GEMINI_1_0_PRO,
            new CompletionEventListener<>() {
              @Override
              public void onError(ErrorDetails error, Throwable t) {
                assertThat(error.getCode()).isEqualTo("400");
                assertThat(error.getMessage()).isEqualTo(
                    "API key not valid. Please pass a valid API key.");
                assertThat(error.getType()).isEqualTo("INVALID_ARGUMENT");
                errorMessageBuilder.append(error.getMessage());
              }
            });

    await().atMost(5, SECONDS)
        .until(() -> "API key not valid. Please pass a valid API key.".contentEquals(
            errorMessageBuilder));
  }

  @Test
  void shouldHandleUnknownApiError() {
    var errorMessageBuilder = new StringBuilder();
    var errorResponse = jsonMapResponse("error_details", "Server error");
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/gemini-1.0-pro:streamGenerateContent");
      return new ResponseEntity(500, errorResponse);
    });

    new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getChatCompletionAsync(
            new GoogleCompletionRequest.Builder(
                List.of(new GoogleCompletionContent("user", List.of("TEST_PROMPT"))))
                .build(),
            GoogleModel.GEMINI_1_0_PRO,
            new CompletionEventListener<>() {
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

  static Stream<Arguments> testEmbeddings() {
    double[] one = {1};
    var embeddingOne = jsonMapResponse("embedding", jsonMap("values", one));
    var embeddingNull = jsonMapResponse("embedding", jsonMap("values", null));
    return Stream.of(
        Arguments.of("{}", null),
        Arguments.of(jsonMapResponse("embedding", null), null),
        Arguments.of(embeddingOne, one),
        Arguments.of(embeddingNull, null)
    );
  }

  @ParameterizedTest
  @MethodSource("testEmbeddings")
  void shouldGetEmbeddings(String json, double[] expected) {
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/text-embedding-004:embedContent");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getUri().getQuery()).isEqualTo("key=TEST_API_KEY");
      return new ResponseEntity(200, json);
    });

    var result = new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getEmbedding(List.of("TEST_PROMPT"), GoogleModel.TEXT_EMBEDDING_004);

    assertThat(result).isEqualTo(expected);
  }

  static Stream<Arguments> testBatchEmbeddings() {
    double[] one = {1};
    var embeddingsOne = jsonMapResponse("embeddings",
        jsonArray(jsonMap("values", one)));
    var embeddingsNull = jsonMapResponse("embeddings", null);
    double[] two = {2};
    var embeddingsTwo = jsonMapResponse("embeddings",
        jsonArray(
            jsonMap("values", one),
            jsonMap("values", two)
        ));
    return Stream.of(
        Arguments.of("{}", null),
        Arguments.of(jsonMapResponse("embeddings", null), null),
        Arguments.of(embeddingsOne, List.of(one)),
        Arguments.of(embeddingsNull, null),
        Arguments.of(embeddingsTwo, List.of(one, two))
    );
  }

  @ParameterizedTest
  @MethodSource("testBatchEmbeddings")
  void shouldGetBatchEmbeddings(String json, List<double[]> expected) {
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/text-embedding-004:batchEmbedContents");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getUri().getQuery()).isEqualTo("key=TEST_API_KEY");
      return new ResponseEntity(200, json);
    });

    var result = new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getBatchEmbeddings(List.of(new GoogleEmbeddingContentRequest("TEST_PROMPT",
            GoogleModel.TEXT_EMBEDDING_004.getCode())), GoogleModel.TEXT_EMBEDDING_004);
    if (expected == null) {
      assertThat(result).isNull();
    } else {
      assertThat(result).containsExactlyElementsOf(expected);
    }
  }


  @Test
  void shouldGetModels() {
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models");
      assertThat(request.getUri().getQuery()).isEqualTo("pageSize=1&key=TEST_API_KEY");
      assertThat(request.getMethod()).isEqualTo("GET");
      return new ResponseEntity(new ObjectMapper().writeValueAsString(
          Map.of("models", List.of(Map.of("name", "gemini-1.5-pro",
              "displayName", "Gemini Pro 1.5",
              "supportedGenerationMethods", List.of("generateContent", "embedContent"))))));
    });

    var response = new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getModels(1, null);

    assertThat(response.getModels())
        .hasSize(1)
        .first()
        .extracting("name", "displayName", "supportedGenerationMethods")
        .containsExactly("gemini-1.5-pro", "Gemini Pro 1.5",
            List.of("generateContent", "embedContent"));
  }

  @Test
  void shouldGetModel() {
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/gemini-1.5-pro");
      assertThat(request.getUri().getQuery()).isEqualTo("key=TEST_API_KEY");
      assertThat(request.getMethod()).isEqualTo("GET");
      return new ResponseEntity(new ObjectMapper().writeValueAsString(
          Map.of("name", "gemini-1.5-pro",
              "displayName", "Gemini Pro 1.5",
              "supportedGenerationMethods", List.of("generateContent", "embedContent"))));
    });

    var response = new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getModel("gemini-1.5-pro");

    assertThat(response)
        .extracting("name", "displayName", "supportedGenerationMethods")
        .containsExactly("gemini-1.5-pro", "Gemini Pro 1.5",
            List.of("generateContent", "embedContent"));
  }

  @Test
  void shouldGetCountTokens() {
    expectGoogle((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo(
          "/v1/models/gemini-1.0-pro:countTokens");
      assertThat(request.getUri().getQuery()).isEqualTo("key=TEST_API_KEY");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("contents")
          .isEqualTo( List.of(Map.of("parts", List.of(Map.of("text", "test")))));
      return new ResponseEntity(new ObjectMapper().writeValueAsString(
          Map.of("totalTokens", 1)));
    });

    var response = new GoogleClient.Builder("TEST_API_KEY")
        .build()
        .getCountTokens(List.of(new GoogleCompletionContent(List.of("test"))), GoogleModel.GEMINI_1_0_PRO);

    assertThat(response.getTotalTokens()).isEqualTo(1);
  }
}
