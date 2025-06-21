package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.codegpt.CodeGPTClient;
import ee.carlrobert.llm.client.codegpt.request.CodeCompletionRequest;
import ee.carlrobert.llm.client.codegpt.request.chat.AdditionalRequestContext;
import ee.carlrobert.llm.client.codegpt.request.chat.ChatCompletionRequest;
import ee.carlrobert.llm.client.codegpt.request.chat.ContextFile;
import ee.carlrobert.llm.client.codegpt.request.chat.DocumentationDetails;
import ee.carlrobert.llm.client.codegpt.request.chat.Metadata;
import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

public class CodeGPTClientTest extends BaseTest {

  @Test
  void shouldStreamChatCompletion() {
    var sessionId = UUID.randomUUID();
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
              "maxTokens",
              "messages",
              "webSearchIncluded",
              "documentationDetails",
              "context",
              "metadata")
          .containsExactly(
              "TEST_MODEL",
              0.5,
              true,
              500,
              List.of(Map.of("role", "user", "content", "TEST_PROMPT")),
              true,
              Map.of("name", "TEST_DOC_NAME", "url", "TEST_DOC_URL"),
              Map.of(
                  "files", List.of(Map.of(
                      "name", "TEST_FILE_NAME",
                      "path", "TEST_FILE_PATH",
                      "content", "TEST_FILE_CONTENT")),
                  "conversationsHistory", "TEST_CONVERSATIONS_HISTORY"),
              Map.of(
                  "pluginVersion", "TEST_PLUGIN_VERSION",
                  "platformVersion", "TEST_PLATFORM_VERSION"));
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hello")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))));
    });
    var resultMessageBuilder = new StringBuilder();

    new CodeGPTClient("TEST_API_KEY")
        .getChatCompletionAsync(
            new ChatCompletionRequest.Builder(
                List.of(new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")))
                .setModel("TEST_MODEL")
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setSessionId(sessionId)
                .setMetadata(new Metadata("TEST_PLUGIN_VERSION", "TEST_PLATFORM_VERSION"))
                .setStream(true)
                .setWebSearchIncluded(true)
                .setContext(
                    new AdditionalRequestContext(List.of(
                        new ContextFile("TEST_FILE_NAME", "TEST_FILE_PATH", "TEST_FILE_CONTENT")),
                        "TEST_CONVERSATIONS_HISTORY"))
                .setDocumentationDetails(new DocumentationDetails("TEST_DOC_NAME", "TEST_DOC_URL"))
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
  void shouldStreamCodeCompletion() {
    var resultMessageBuilder = new StringBuilder();
    expectCodeGPT((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/code/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0)).isEqualTo("Bearer TEST_API_KEY");
      assertThat(request.getHeaders().get("X-llm-application-tag").get(0))
          .isEqualTo("codegpt");
      assertThat(request.getBody())
          .extracting(
              "model",
              "prefix",
              "suffix",
              "cursorOffset",
              "fileExtension",
              "fileContent")
          .containsExactly(
              "TEST_MODEL",
              "TEST_PREFIX",
              "TEST_SUFFIX",
              10,
              "txt",
              "TEST_FILE_CONTENT");
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
        .getCodeCompletionAsync(new CodeCompletionRequest.Builder()
                .setPrefix("TEST_PREFIX")
                .setSuffix("TEST_SUFFIX")
                .setModel("TEST_MODEL")
                .setFileExtension("txt")
                .setFileContent("TEST_FILE_CONTENT")
                .setCursorOffset(10)
                .build(),
            new CompletionEventListener<>() {
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
              "maxTokens",
              "messages")
          .containsExactly(
              "TEST_MODEL",
              0.5,
              false,
              500,
              List.of(Map.of("role", "user", "content", "TEST_PROMPT")));

      return new ResponseEntity(new ObjectMapper().writeValueAsString(Map.of("choices", List.of(
          Map.of("message", Map.of(
              "role", "assistant",
              "content", "This is a test"))))));
    });

    var response = new CodeGPTClient("TEST_API_KEY")
        .getChatCompletion(new ChatCompletionRequest.Builder(
            List.of(new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")))
            .setModel("TEST_MODEL")
            .setMaxTokens(500)
            .setTemperature(0.5)
            .setStream(false)
            .build());

    assertThat(response.getChoices())
        .extracting("message")
        .extracting("role", "content")
        .containsExactly(tuple("assistant", "This is a test"));
  }
}
