package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonArray;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMap;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.NdJsonStreamHttpExchange;
import ee.carlrobert.llm.client.ollama.OllamaClient;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaEmbeddingRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaPullRequest;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaPullResponse;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionAssistantMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionToolMessage;
import ee.carlrobert.llm.client.openai.completion.request.Tool;
import ee.carlrobert.llm.client.openai.completion.request.ToolFunction;
import ee.carlrobert.llm.client.openai.completion.request.ToolFunctionParameters;
import ee.carlrobert.llm.client.openai.completion.response.ToolCall;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

public class OllamaClientTest extends BaseTest {

  private static final OllamaClient client = new OllamaClient.Builder().build();

  @Test
  void shouldStreamOllamaChatCompletion() {
    expectOllama((NdJsonStreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("model", "messages", "temperature", "stream")
          .containsExactly("codellama:7b",
              List.of(Map.of("role", "user", "content", "TEST_PROMPT")),
              0.8,
              true);
      return List.of(
          jsonMapResponse(
              e("model", "codellama:7b"),
              e("choices", jsonArray(
                  jsonMap(
                      e("delta", jsonMap(
                          e("role", "assistant"),
                          e("content", "Hel")
                      ))
                  )
              ))
          ),
          jsonMapResponse(
              e("model", "codellama:7b"),
              e("choices", jsonArray(
                  jsonMap(
                      e("delta", jsonMap(
                          e("content", "lo")
                      ))
                  )
              ))
          ),
          jsonMapResponse(
              e("model", "codellama:7b"),
              e("choices", jsonArray(
                  jsonMap(
                      e("delta", jsonMap(
                          e("content", "!")
                      ))
                  )
              ))
          ));
    });

    var resultMessageBuilder = new StringBuilder();
    client.getChatCompletionAsync(
        new OpenAIChatCompletionRequest.Builder(
            List.of(
                new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")
            ))
            .setModel("codellama:7b")
            .setStream(true)
            .setTemperature(0.8)
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
  void shouldGetOllamaChatCompletion() {
    expectOllama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("model", "messages", "stream")
          .containsExactly(
              "codellama:7b",
              List.of(
                  Map.of(
                      "role", "user",
                      "content", "TEST_PROMPT"
                  )
              ),
              false
          );
      return new ResponseEntity(
          jsonMapResponse(
              e("model", "codellama:7b"),
              e("choices", jsonArray(
                  jsonMap(
                      e("message", jsonMap(
                          e("role", "assistant"),
                          e("content", "Hello!")
                      ))
                  )
              ))
          )
      );
    });

    var response = client.getChatCompletion(new OpenAIChatCompletionRequest.Builder(
        List.of(
            new OpenAIChatCompletionStandardMessage("user", "TEST_PROMPT")
        )
    )
        .setModel("codellama:7b")
        .setStream(false)
        .build());

    assertThat(response.getChoices().get(0).getMessage().getContent()).isEqualTo("Hello!");
  }

  @Test
  void shouldGetOllamaTags() {
    expectOllama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/tags");
      assertThat(request.getMethod()).isEqualTo("GET");
      return new ResponseEntity(jsonMapResponse("models", jsonArray(
          Map.of("name", "codellama:7b",
              "size", 70000,
              "details", jsonMap(e("format", "gguf"), e("parameter_size", "7B"))),
          Map.of("name", "codellama:13b",
              "size", 130000,
              "details", jsonMap(e("format", "gguf"), e("parameter_size", "13B")))
      )));
    });

    var response = client.getModelTags();

    assertThat(response.getModels()).hasSize(2);
    OllamaModel firstModel = response.getModels().get(0);
    assertThat(firstModel.getName()).isEqualTo("codellama:7b");
    assertThat(firstModel.getSize()).isEqualTo(70000);
    assertThat(firstModel.getDetails().getParameterSize()).isEqualTo("7B");
    OllamaModel secondModel = response.getModels().get(1);
    assertThat(secondModel.getName()).isEqualTo("codellama:13b");
    assertThat(secondModel.getSize()).isEqualTo(130000);
    assertThat(secondModel.getDetails().getParameterSize()).isEqualTo("13B");
  }

  @Test
  void shouldPullOllamaModel() {
    expectOllama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/pull");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("name", "stream")
          .containsExactly("codellama:7b", false);
      return new ResponseEntity(jsonMapResponse("status", "success"));
    });

    var response = client.pullModel(new OllamaPullRequest("codellama:7b", false));

    assertThat(response.getStatus()).isEqualTo("success");
  }

  @Test
  void shouldStreamPullOllamaModel() {
    expectOllama((NdJsonStreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/pull");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("name", "stream")
          .containsExactly("codellama:7b", true);
      return List.of(
          jsonMapResponse(
              e("status", "downloading digestname"),
              e("digest", "digestname"),
              e("total", "1234"),
              e("completed", "10")),
          jsonMapResponse(e("status", "verifying sha256 digestname")),
          jsonMapResponse(e("status", "writing manifest")),
          jsonMapResponse(e("status", "removing any unused layers")),
          jsonMapResponse(e("status", "success")));
    });

    var results = new ArrayList<OllamaPullResponse>();
    client.pullModelAsync(new OllamaPullRequest("codellama:7b", true),
        new CompletionEventListener<>() {
          @Override
          public void onMessage(OllamaPullResponse response, EventSource eventSource) {
            results.add(response);
          }
        });

    await().atMost(5, SECONDS).until(() -> results.size() == 5);
    OllamaPullResponse firstResponse = results.get(0);
    assertThat(firstResponse.getStatus()).isEqualTo("downloading digestname");
    assertThat(firstResponse.getDigest()).isEqualTo("digestname");
    assertThat(firstResponse.getTotal()).isEqualTo(1234);
    assertThat(firstResponse.getCompleted()).isEqualTo(10);
    assertThat(results.get(1).getStatus()).isEqualTo("verifying sha256 digestname");
    assertThat(results.get(2).getStatus()).isEqualTo("writing manifest");
    assertThat(results.get(3).getStatus()).isEqualTo("removing any unused layers");
    assertThat(results.get(4).getStatus()).isEqualTo("success");
  }

  @Test
  void shouldDeleteOllamaModel() {
    expectOllama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/delete");
      assertThat(request.getMethod()).isEqualTo("DELETE");
      assertThat(request.getBody()).extracting("name")
          .isEqualTo("codellama:7b");
      return new ResponseEntity(null);
    });

    var response = client.deleteModel("codellama:7b");

    assertThat(response).isTrue();
  }

  @Test
  void shouldGetOllamaModelInfo() {
    expectOllama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/show");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody()).extracting("name")
          .isEqualTo("codellama:7b");
      return new ResponseEntity(jsonMapResponse(
          e("modelfile", "# Modelfile generated by \"ollama show\"\n"),
          e("template", "{{ .System }}\nUSER: {{ .Prompt }}\nASSSISTANT: "),
          e("parameters", "num_ctx                        4096"),
          e("details", jsonMap(e("format", "gguf"), e("parameter_size", "7B")))));
    });

    var response = client.getModelInfo("codellama:7b");

    assertThat(response.getModelfile()).isEqualTo("# Modelfile generated by \"ollama show\"\n");
    assertThat(response.getTemplate()).isEqualTo(
        "{{ .System }}\nUSER: {{ .Prompt }}\nASSSISTANT: ");
    assertThat(response.getParameters()).isEqualTo("num_ctx                        4096");
    assertThat(response.getDetails().getFormat()).isEqualTo("gguf");
    assertThat(response.getDetails().getParameterSize()).isEqualTo("7B");
  }

  @Test
  void shouldGetOllamaEmbedding() {
    double[] embedding = {0.1, 0.2, 0.3};
    expectOllama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/api/embeddings");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("model", "prompt")
          .containsExactly("codellama:7b", "This is a prompt");
      return new ResponseEntity(jsonMapResponse(e("embedding", embedding)));
    });

    var response = client.getEmbedding(
        new OllamaEmbeddingRequest.Builder("codellama:7b", "This is a prompt").build());

    assertThat(response.getEmbedding()).isEqualTo(embedding);
  }
}