package ee.carlrobert.llm.client;

import static ee.carlrobert.llm.client.util.JSONUtil.e;
import static ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.awaitility.Awaitility.await;

import ee.carlrobert.llm.client.http.ResponseEntity;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.llama.LlamaClient;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest.Builder;
import ee.carlrobert.llm.client.llama.completion.LlamaInfillRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionAssistantMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionToolMessage;
import ee.carlrobert.llm.client.openai.completion.request.Tool;
import ee.carlrobert.llm.client.openai.completion.request.ToolFunction;
import ee.carlrobert.llm.client.openai.completion.request.ToolFunctionParameters;
import ee.carlrobert.llm.client.openai.completion.response.ToolCall;
import ee.carlrobert.llm.client.openai.completion.response.ToolFunctionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

public class LlamaClientTest extends BaseTest {

  @Test
  void shouldGetLlamaInfill() {
    expectLlama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/infill");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("input_prefix", "input_suffix", "stream", "n_predict", "temperature", "top_k",
              "top_p", "min_p",
              "repeat_penalty", "stop")
          .containsExactly("PREFIX", "SUFFIX", false, 10, 0.5, 40, 0.9, 0.05, 1.1,
              List.of("  <EOT>", "<EOT>"));
      return new ResponseEntity(jsonMapResponse("content", "Hello!"));
    });

    var response = new LlamaClient.Builder()
        .build()
        .getInfill(new LlamaInfillRequest(new Builder("")
            .setStream(false)
            .setN_predict(10)
            .setTemperature(0.5)
            .setTop_k(40)
            .setTop_p(0.9)
            .setMin_p(0.05)
            .setRepeat_penalty(1.1)
            .setStop(List.of("  <EOT>", "<EOT>")), "PREFIX", "SUFFIX")
        );

    assertThat(response.getContent()).isEqualTo("Hello!");
  }

  @Test
  void shouldStreamLlamaInfill() {
    var resultMessageBuilder = new StringBuilder();

    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/infill");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("input_prefix", "input_suffix", "stream", "n_predict", "stop")
          .containsExactly("TEST_PREFIX", "TEST_SUFFIX", true, 10, List.of("  <EOT>", "<EOT>"));
      assertThat(request.getHeaders())
          .flatExtracting("Accept", "Connection")
          .containsExactly("text/event-stream", "Keep-Alive");
      return List.of(
          jsonMapResponse("content", "Hel"),
          jsonMapResponse("content", "lo"),
          jsonMapResponse("content", "!"));
    });

    new LlamaClient.Builder()
        .build()
        .getInfillAsync(
            new LlamaInfillRequest(new Builder("")
                .setStream(true)
                .setN_predict(10)
                .setStop(List.of("  <EOT>", "<EOT>")),
                "TEST_PREFIX",
                "TEST_SUFFIX"),
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
    var messages = List.<OpenAIChatCompletionMessage>of(
        new OpenAIChatCompletionStandardMessage("system", "You are a helpful assistant."),
        new OpenAIChatCompletionStandardMessage("user", "Hello!")
    );
    expectLlama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody())
          .extracting("messages", "stream", "temperature")
          .containsExactly(
              List.of(
                  Map.of("role", "system", "content", "You are a helpful assistant."),
                  Map.of("role", "user", "content", "Hello!")
              ),
              false,
              0.7
          );
      return new ResponseEntity(jsonMapResponse(
          e("choices", List.of(Map.of(
              "index", 0,
              "message", Map.of(
                  "role", "assistant",
                  "content", "Hi there! How can I help you today?"
              ),
              "finish_reason", "stop"
          ))),
          e("usage", Map.of("total_tokens", 50))
      ));
    });

    var response = new LlamaClient.Builder()
        .build()
        .getChatCompletion(new OpenAIChatCompletionRequest.Builder(messages)
            .setStream(false)
            .setTemperature(0.7)
            .build());

    assertThat(response.getChoices()).hasSize(1);
    assertThat(response.getChoices().get(0).getMessage().getContent())
        .isEqualTo("Hi there! How can I help you today?");
  }

  @Test
  void shouldStreamChatCompletionWithTools() {
    var tool = new Tool();
    tool.setType("function");
    var function = new ToolFunction();
    function.setName("get_weather");
    function.setDescription("Get the current weather in a given location");
    var parameters = new ToolFunctionParameters();
    parameters.setType("object");
    parameters.setProperties(Map.of(
        "location", Map.of(
            "type", "string",
            "description", "The city and state, e.g. San Francisco, CA"
        )
    ));
    parameters.setRequired(List.of("location"));
    function.setParameters(parameters);
    tool.setFunction(function);
    var messages = List.<OpenAIChatCompletionMessage>of(
        new OpenAIChatCompletionStandardMessage("user", "What's the weather in New York?")
    );
    final var toolCalls = new ArrayList<ToolCall>();
    final var completionReceived = new AtomicBoolean(false);

    expectLlama((StreamHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getBody()).containsKey("tools");
      assertThat(request.getBody().get("tools")).asInstanceOf(LIST).hasSize(1);
      assertThat(request.getHeaders())
          .flatExtracting("Accept", "Connection")
          .containsExactly("text/event-stream", "Keep-Alive");
      var response1 = new HashMap<String, Object>();
      response1.put("index", 0);
      var toolCall1 = new HashMap<String, Object>();
      toolCall1.put("index", 0);
      toolCall1.put("id", "call_123");
      toolCall1.put("type", "function");
      var function1 = new HashMap<String, Object>();
      function1.put("name", "get_weather");
      function1.put("arguments", "{\"location\": \"New York\"}");
      toolCall1.put("function", function1);
      var delta1 = new HashMap<String, Object>();
      delta1.put("tool_calls", List.of(toolCall1));
      response1.put("delta", delta1);
      response1.put("finish_reason", null);

      var response2 = new HashMap<String, Object>();
      response2.put("index", 0);
      response2.put("delta", new HashMap<String, Object>());
      response2.put("finish_reason", "tool_calls");

      return List.of(
          jsonMapResponse("choices", List.of(response1)),
          jsonMapResponse("choices", List.of(response2))
      );
    });

    new LlamaClient.Builder()
        .build()
        .getChatCompletionAsync(
            new OpenAIChatCompletionRequest.Builder(messages)
                .setTools(List.of(tool))
                .setToolChoice("auto")
                .build(),
            new CompletionEventListener<>() {
              @Override
              public void onMessage(String message, EventSource eventSource) {
              }

              @Override
              public void onToolCall(ToolCall toolCall) {
                toolCalls.add(toolCall);
              }

              @Override
              public void onComplete(StringBuilder messageBuilder) {
                completionReceived.set(true);
              }
            });

    await().atMost(5, SECONDS).until(completionReceived::get);

    assertThat(toolCalls).hasSize(1);
    assertThat(toolCalls.get(0).getId()).isEqualTo("call_123");
    assertThat(toolCalls.get(0).getFunction().getName()).isEqualTo("get_weather");
    assertThat(toolCalls.get(0).getFunction().getArguments())
        .isEqualTo("{\"location\": \"New York\"}");
  }

  @Test
  void shouldHandleToolMessageInConversation() {
    var messages = new ArrayList<OpenAIChatCompletionMessage>();
    messages.add(new OpenAIChatCompletionStandardMessage("user",
        "What's the weather in New York?"));
    messages.add(new OpenAIChatCompletionStandardMessage("assistant",
        "I'll check the weather for you."));
    var toolCall = new ToolCall(0, "call_123", "function",
        new ToolFunctionResponse("get_weather", "{\"location\": \"New York\"}"));
    var toolCallMessage = new OpenAIChatCompletionAssistantMessage(null, List.of(toolCall));
    messages.add(toolCallMessage);
    messages.add(new OpenAIChatCompletionToolMessage("72F and sunny", "call_123"));

    expectLlama((BasicHttpExchange) request -> {
      assertThat(request.getUri().getPath()).isEqualTo("/v1/chat/completions");
      assertThat(request.getMethod()).isEqualTo("POST");
      var requestMessages = (List<Map<String, Object>>) request.getBody().get("messages");
      assertThat(requestMessages).hasSize(4);
      assertThat(requestMessages.get(3))
          .containsEntry("role", "tool")
          .containsEntry("content", "72F and sunny")
          .containsEntry("tool_call_id", "call_123");

      return new ResponseEntity(jsonMapResponse(
          e("choices", List.of(Map.of(
              "index", 0,
              "message", Map.of(
                  "role", "assistant",
                  "content",
                  "The weather in New York is currently 72F and sunny. It's a beautiful day!"
              ),
              "finish_reason", "stop"
          )))
      ));
    });

    var response = new LlamaClient.Builder()
        .build()
        .getChatCompletion(new OpenAIChatCompletionRequest.Builder(messages)
            .setStream(false)
            .setTemperature(0.7)
            .build());

    assertThat(response.getChoices()).hasSize(1);
    assertThat(response.getChoices().get(0).getMessage().getContent())
        .contains("72F and sunny");
  }
}