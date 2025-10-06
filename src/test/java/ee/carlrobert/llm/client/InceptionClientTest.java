package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.inception.InceptionClient;
import ee.carlrobert.llm.client.inception.request.InceptionApplyRequest;
import ee.carlrobert.llm.client.inception.request.InceptionFIMRequest;
import ee.carlrobert.llm.client.inception.request.InceptionNextEditRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.sse.EventSource;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

public class InceptionClientTest extends BaseTest {

  @Test
  void shouldPostChatCompletions() {
    expectInception((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_INCEPTION_API_KEY");
      assertThat(request.getBody())
          .extracting("model", "messages")
          .containsExactly(
              "mercury",
              List.of(Map.of("role", "user", "content", "What is a diffusion model?"))
          );
      return new ResponseEntity(new ObjectMapper().writeValueAsString(Map.of(
          "choices", List.of(Map.of("message", Map.of(
              "role", "assistant",
              "content", "A diffusion model is..."))))));
    });
    var request = new OpenAIChatCompletionRequest.Builder(
        List.of(new OpenAIChatCompletionStandardMessage(
            "user",
            "What is a diffusion model?")))
        .setModel("mercury")
        .setStream(false)
        .build();
    var client = new InceptionClient.Builder("TEST_INCEPTION_API_KEY").build();

    var response = client.getChatCompletion(request);

    assertThat(response.getChoices()).hasSize(1);
    assertThat(response.getChoices().get(0).getMessage().getContent()).contains("diffusion model");
  }

  @Test
  void shouldStreamChatCompletions() {
    var prompt = "TEST_PROMPT";
    var result = new StringBuilder();
    expectInception((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_INCEPTION_API_KEY");
      assertThat(request.getHeaders().get("Accept").get(0))
          .isEqualTo("text/event-stream");
      assertThat(request.getBody())
          .extracting("model", "stream", "messages")
          .containsExactly(
              "mercury",
              true,
              List.of(Map.of("role", "user", "content", prompt))
          );
      return java.util.List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hello")))),
          jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!"))))
      );
    });
    var request = new OpenAIChatCompletionRequest.Builder(
        List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
        .setModel("mercury")
        .setStream(true)
        .build();
    var client = new InceptionClient.Builder("TEST_INCEPTION_API_KEY").build();

    client.getChatCompletionAsync(
        request,
        new CompletionEventListener<>() {
          @Override
          public void onMessage(String message, EventSource eventSource) {
            result.append(message);
          }
        });

    Awaitility.await()
        .atMost(TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS)
        .until(() -> "Hello!".contentEquals(result));
  }

  @Test
  void shouldStreamFIMCompletions() {
    expectInception((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/fim/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_INCEPTION_API_KEY");
      assertThat(request.getHeaders().get("Accept").get(0))
          .isEqualTo("text/event-stream");
      assertThat(request.getBody())
          .extracting("model", "prompt", "suffix", "stream")
          .containsExactly("mercury-coder", "def fibonacci(", "return a + b", true);
      return List.of(
          jsonMapResponse("choices", jsonArray(jsonMap("text", "def fibonacci(n): "))),
          jsonMapResponse("choices", jsonArray(jsonMap("text", "return a + b")))
      );
    });
    var result = new StringBuilder();
    var request = new InceptionFIMRequest.Builder()
        .setModel("mercury-coder")
        .setPrompt("def fibonacci(")
        .setSuffix("return a + b")
        .build();
    var client = new InceptionClient.Builder("TEST_INCEPTION_API_KEY").build();

    client.getFimCompletionAsync(
        request,
        new CompletionEventListener<>() {
          @Override
          public void onMessage(String message, EventSource eventSource) {
            result.append(message);
          }
        });

    Awaitility.await()
        .atMost(TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS)
        .until(() -> result.toString().contains("def fibonacci"));
  }

  @Test
  void shouldPostApplyEditCompletions() {
    var content = String.join("\n",
        "<|original_code|>",
        "class Calculator:",
        "    \"\"\"A simple calculator class.\"\"\"",
        "    def __init__(self):",
        "        self.history = []",
        "",
        "    def add(self, a, b):",
        "        \"\"\"Adds two numbers.\"\"\"",
        "        result = a + b",
        "        return result",
        "<|/original_code|>",
        "",
        "<|update_snippet|>",
        "// ... existing code ...",
        "def multiply(self, a, b):",
        "    \"\"\"Multiplies two numbers.\"\"\"",
        "    result = a * b",
        "    return result",
        "// ... existing code ...",
        "<|/update_snippet|>");
    expectInception((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/apply/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_INCEPTION_API_KEY");
      assertThat(request.getBody())
          .extracting("model", "messages")
          .satisfies(values -> assertThat(values).isNotNull());
      return new ResponseEntity(new ObjectMapper().writeValueAsString(Map.of(
          "choices", List.of(Map.of("message", Map.of(
              "role", "assistant",
              "content", "class Calculator: ... def multiply(self, a, b): ..."))))));
    });
    var client = new InceptionClient.Builder("TEST_INCEPTION_API_KEY").build();
    var request = new InceptionApplyRequest.Builder()
        .setModel("mercury-coder")
        .setMessages(List.of(new OpenAIChatCompletionStandardMessage("user", content)))
        .build();

    var response = client.getApplyEditCompletion(request);

    assertThat(response.getChoices()).hasSize(1);
    assertThat(response.getChoices().get(0).getMessage().getContent()).contains("multiply");
  }

  @Test
  void shouldPostNextEditCompletions() {
    var content = String.join("\n",
        "<|recently_viewed_code_snippets|>",
        "",
        "<|/recently_viewed_code_snippets|>",
        "",
        "<|current_file_content|>",
        "current_file_path: solver.py",
        "'''''''''",
        "function: flagAllNeighbors",
        "----------",
        "This function marks each of the covered neighbors of the cell at the given row",
        "<|code_to_edit|>",
        "and col as flagged.",
        "'''''''''",
        "def flagAllNeighbors(board<|cursor|>, row, col): ",
        " for r, c in b.getNeighbors(row, col):",
        "   if b.isValid(r, c):",
        "     b.flag(r, c)",
        "",
        "<|/code_to_edit|>",
        "<|/current_file_content|>",
        "",
        "<|edit_diff_history|>",
        "--- /c:/Users/test/testing/solver.py",
        "+++ /c:/Users/test/testing/solver.py",
        "@@ -6,1 +6,1 @@",
        "-def flagAllNeighbors(b, row, col): ",
        "+def flagAllNeighbors(board, row, col): ",
        "",
        "<|/edit_diff_history|>");
    expectInception((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/edit/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getHeaders().get("Authorization").get(0))
          .isEqualTo("Bearer TEST_INCEPTION_API_KEY");
      assertThat(request.getBody())
          .extracting("model", "messages")
          .satisfies(values -> assertThat(values).isNotNull());
      return new ResponseEntity(new ObjectMapper().writeValueAsString(Map.of(
          "choices", List.of(Map.of("message", Map.of(
              "role", "assistant",
              "content", "def flagAllNeighbors(board, row, col): ..."))))));
    });
    var request = new InceptionNextEditRequest.Builder()
        .setModel("mercury-coder")
        .setMessages(List.of(new OpenAIChatCompletionStandardMessage("user", content)))
        .build();
    var client = new InceptionClient.Builder("TEST_INCEPTION_API_KEY").build();

    var response = client.getNextEditCompletion(request);

    assertThat(response.getChoices()).hasSize(1);
    assertThat(response.getChoices().get(0).getMessage().getContent()).contains("flagAllNeighbors");
  }
}
