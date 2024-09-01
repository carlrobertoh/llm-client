package ee.carlrobert.llm.client.ollama;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;
import static ee.carlrobert.llm.client.InterceptorUtil.REWRITE_X_NDJSON_CONTENT_INTERCEPTOR;
import static java.lang.String.format;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaEmbeddingRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaPullRequest;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaChatCompletionResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaCompletionResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaEmbeddingResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModelInfoResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaPullResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaTagsResponse;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;

public class OllamaClient {

  private static final String BASE_URL = PropertiesLoader.getValue("ollama.baseUrl");
  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

  private final OkHttpClient httpClient;
  private final String host;
  private final Integer port;
  private final String apiKey;

  protected OllamaClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder
        .addInterceptor(REWRITE_X_NDJSON_CONTENT_INTERCEPTOR)
        .build();
    this.host = builder.host;
    this.port = builder.port;
    this.apiKey = builder.apiKey;
  }

  private static RequestBody createRequestBody(Object request) throws JsonProcessingException {
    return RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON);
  }

  public EventSource getCompletionAsync(
      OllamaCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    var responseStreamManager = new ResponseStreamManager();
    var eventSource =
        getEventSource(() -> responseStreamManager.cancelStream(eventListener));

    CompletableFuture.runAsync(() -> {
      try {
        processStreamRequest(
            buildPostHttpRequest(request, "/api/generate"),
            eventListener,
            eventSource,
            responseStreamManager,
            message -> {
              OllamaCompletionResponse response;
              try {
                response = new ObjectMapper().readValue(message, OllamaCompletionResponse.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
              return new Pair<>(response.getResponse(), response.isDone());
            });
      } catch (IOException e) {
        eventListener.onError(new ErrorDetails("Something went wrong"), e);
      }
    });
    return eventSource;
  }

  public EventSource getChatCompletionAsync(
      OllamaChatCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    var responseStreamManager = new ResponseStreamManager();
    var eventSource =
        getEventSource(() -> responseStreamManager.cancelStream(eventListener));

    CompletableFuture.runAsync(
        () -> {
          try {
            processStreamRequest(
                buildPostHttpRequest(request, "/api/chat"),
                eventListener,
                eventSource,
                responseStreamManager,
                message -> {
                  try {
                    var response =
                        new ObjectMapper().readValue(message, OllamaChatCompletionResponse.class);
                    return new Pair<>(response.getMessage().getContent(), response.isDone());
                  } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                  }
                });
          } catch (IOException e) {
            eventListener.onError(new ErrorDetails("Something went wrong"), e);
          }
        });
    return eventSource;
  }

  public OllamaCompletionResponse getCompletion(OllamaCompletionRequest request) {
    try (var response = httpClient.newCall(buildPostRequest(request, "/api/generate")).execute()) {
      return DeserializationUtil.mapResponse(response, OllamaCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama completion for the given request:\n" + request, e);
    }
  }

  public OllamaChatCompletionResponse getChatCompletion(OllamaChatCompletionRequest request) {
    try (var response = httpClient.newCall(buildPostRequest(request, "/api/chat")).execute()) {
      return DeserializationUtil.mapResponse(response, OllamaChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama chat completion for the given request:\n" + request, e);
    }
  }

  public OllamaEmbeddingResponse getEmbedding(OllamaEmbeddingRequest request) {
    try (var response = httpClient
        .newCall(buildPostRequest(request, "/api/embeddings"))
        .execute()) {
      return DeserializationUtil.mapResponse(response, OllamaEmbeddingResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama embedding for the given request:\n" + request, e);
    }
  }

  public OllamaPullResponse pullModel(OllamaPullRequest request) {
    try (var response = httpClient.newCall(buildPostRequest(request, "/api/pull")).execute()) {
      return DeserializationUtil.mapResponse(response, OllamaPullResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not pull ollama model for the given request:\n" + request, e);
    }
  }

  public EventSource pullModelAsync(
      OllamaPullRequest request,
      CompletionEventListener<OllamaPullResponse> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(
            buildPostRequest(request, "/api/pull", true),
            getPullModelEventSourceListener(eventListener));
  }

  public boolean deleteModel(String model) {
    try (var response = httpClient
        .newCall(defaultRequest("/api/delete")
            .delete(createRequestBody(Map.of("name", model)))
            .build())
        .execute()) {
      return response.isSuccessful();
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not delete ollama model for the given model name:\n" + model, e);
    }
  }

  public OllamaTagsResponse getModelTags() {
    try (var response = httpClient
        .newCall(defaultRequest("/api/tags").get().build())
        .execute()) {
      return DeserializationUtil.mapResponse(response, OllamaTagsResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama model tags:\n", e);
    }
  }

  public OllamaModelInfoResponse getModelInfo(String model) {
    try (var response = httpClient
        .newCall(buildPostRequest(Map.of("name", model), "/api/show"))
        .execute()) {
      return DeserializationUtil.mapResponse(response, OllamaModelInfoResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama model info for the given model name:\n" + model, e);
    }
  }

  private void processStreamRequest(
      HttpRequest request,
      CompletionEventListener<String> eventListener,
      EventSource eventSource,
      ResponseStreamManager responseStreamManager,
      Function<String, Pair<String, Boolean>> onMessageReceived
  ) {
    try {
      var httpResponse =
          HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
      int statusCode = httpResponse.statusCode();

      var inputStream = httpResponse.body();
      responseStreamManager.setInputStream(inputStream);

      try (var reader = new BufferedReader(
          new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        responseStreamManager.processStream(reader, onMessageReceived, eventListener, eventSource);
      }
      if (statusCode != 200) {
        eventListener.onError(
            new ErrorDetails("The request failed with status code " + statusCode),
            new RuntimeException());
      }
    } catch (IOException | InterruptedException e) {
      if (!(e instanceof InterruptedException) && !"closed".equals(e.getMessage())) {
        eventListener.onError(new ErrorDetails("Something went wrong"), e);
      }
    }
  }

  private HttpRequest buildPostHttpRequest(
      Object request,
      String path) throws JsonProcessingException {
    return HttpRequest.newBuilder(URI.create(BASE_URL + path))
        .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(request)))
        .header("Content-Type", "application/json")
        .timeout(Duration.ofSeconds(30))
        .build();
  }

  private Request buildPostRequest(Object request, String path) {
    return buildPostRequest(request, path, false);
  }

  private Request buildPostRequest(Object request, String path, boolean stream) {
    try {
      return defaultRequest(path, stream)
          .post(createRequestBody(request))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private Request.Builder defaultRequest(String path) {
    return defaultRequest(path, false);
  }

  private Request.Builder defaultRequest(String path, boolean stream) {
    var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
    var builder = new Request.Builder()
        .url((host == null ? baseHost : host) + path)
        .header("Cache-Control", "no-cache")
        .header("Content-Type", "application/json")
        .header("Accept", stream ? "application/x-ndjson" : "text/json");
    if (apiKey != null) {
      builder.header("Authorization", "Bearer " + apiKey);
    }
    return builder;
  }

  private CompletionEventSourceListener<String> getChatCompletionEventSourceListener(
      CompletionEventListener<String> eventListener
  ) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          return OBJECT_MAPPER.readValue(data, OllamaChatCompletionResponse.class).getMessage()
              .getContent();
        } catch (JacksonException e) {
          return "";
        }
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  private CompletionEventSourceListener<String> getCompletionEventSourceListener(
      CompletionEventListener<String> eventListener) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          return OBJECT_MAPPER.readValue(data, OllamaCompletionResponse.class).getResponse();
        } catch (JacksonException e) {
          return "";
        }
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  private CompletionEventSourceListener<OllamaPullResponse> getPullModelEventSourceListener(
      CompletionEventListener<OllamaPullResponse> eventListener) {
    return new CompletionEventSourceListener<>(eventListener) {
      @Override
      protected OllamaPullResponse getMessage(String data) {
        try {
          return OBJECT_MAPPER.readValue(data, OllamaPullResponse.class);
        } catch (JacksonException e) {
          return null;
        }
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  private EventSource getEventSource(Runnable onCancel) {
    return new EventSource() {
      @NotNull
      @Override
      public Request request() {
        return new Request.Builder().build();
      }

      @Override
      public void cancel() {
        onCancel.run();
      }
    };
  }

  static class ResponseStreamManager {

    private final StringBuilder responseBuffer = new StringBuilder();
    private InputStream responseBodyStream;

    public void setInputStream(InputStream inputStream) {
      this.responseBodyStream = inputStream;
    }

    public void cancelStream(CompletionEventListener<?> eventListener) {
      if (responseBodyStream != null) {
        try {
          responseBodyStream.close();
          eventListener.onCancelled(responseBuffer);
        } catch (IOException e) {
          eventListener.onError(new ErrorDetails("Unable to close stream"), e);
        }
      }
    }

    public void processStream(BufferedReader reader,
        Function<String, Pair<String, Boolean>> onMessageReceived,
        CompletionEventListener<String> eventListener,
        EventSource eventSource) throws IOException {
      String line;
      while ((line = reader.readLine()) != null) {
        var processedContent = onMessageReceived.apply(line);
        var message = processedContent.getFirst();
        var done = processedContent.getSecond();
        eventListener.onMessage(message, eventSource);
        if (!done) {
          responseBuffer.append(message);
        }
      }
      eventListener.onComplete(responseBuffer);
    }
  }

  public static class Builder {

    private String host;
    private Integer port;
    private String apiKey;

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setPort(Integer port) {
      this.port = port;
      return this;
    }

    public Builder setApiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public OllamaClient build(OkHttpClient.Builder builder) {
      return new OllamaClient(this, builder);
    }

    public OllamaClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
