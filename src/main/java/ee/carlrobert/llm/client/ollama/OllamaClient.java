package ee.carlrobert.llm.client.ollama;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.StreamableRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaEmbeddingRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaPullRequest;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaCompletionResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaEmbeddingResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModelInfoResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaPullResponse;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaTagsResponse;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.io.IOException;
import java.util.Map;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;

public class OllamaClient {

  private static final String BASE_URL = PropertiesLoader.getValue("ollama.baseUrl");

  private final OkHttpClient httpClient;
  private final String host;
  private final Integer port;

  protected OllamaClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    httpClientBuilder
        .addInterceptor(new Interceptor() {
          @NotNull
          @Override
          public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            // Workaround since OkHttp does not recognize "application/x-ndjson" as a valid stream
            // Content-Type
            // see: RealEventSource.kt ResponseBody.isEventStream()
            if (response.header("Content-Type", "")
                .equals("application/x-ndjson")) {
              return convertNDJsonToTextEventStreamResponse(response);
            }
            return response;
          }
        });
    this.httpClient = httpClientBuilder.build();
    this.host = builder.host;
    this.port = builder.port;
  }

  @NotNull
  private static Response convertNDJsonToTextEventStreamResponse(Response response)
      throws IOException {
    String ndJsonString = response.body().string();
    String textEventString = ndJsonString
        .replace("{\"model\"", "data: {\"model\"")
        .replace("}\n", "}\n\n");
    return response
        .newBuilder()
        .header("Content-Type", "text/event-stream")
        .body(ResponseBody.create(textEventString, MediaType.get("text/event-stream")))
        .build();
  }

  public EventSource getChatCompletionAsync(
      OllamaCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(buildCompletionHttpRequest(request),
            getCompletionEventSourceListener(eventListener));
  }

  public OllamaCompletionResponse getChatCompletion(OllamaCompletionRequest request) {
    try (var response = httpClient.newCall(buildCompletionHttpRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, OllamaCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama completion for the given request:\n" + request, e);
    }
  }

  public OllamaEmbeddingResponse getEmbedding(OllamaEmbeddingRequest request) {
    try (var response = httpClient.newCall(
            buildPostRequest(request, "/api/embeddings", false))
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
        .newEventSource(buildPostRequest(request, "/api/pull"),
            getPullModelEventSourceListener(eventListener));
  }


  public boolean deleteModel(String model) {
    try (var response = httpClient.newCall(defaultRequest("/api/delete", false)
        .delete(createRequestBody(Map.of("name", model))).build()).execute()) {
      return response.isSuccessful();
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not delete ollama model for the given model name:\n" + model, e);
    }
  }

  public OllamaTagsResponse getModelTags() {
    try (var response = httpClient.newCall(defaultRequest("/api/tags", false).get().build())
        .execute()) {
      return DeserializationUtil.mapResponse(response, OllamaTagsResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama model tags:\n", e);
    }
  }

  public OllamaModelInfoResponse getModelInfo(String model) {
    try (var response = httpClient.newCall(
            buildPostRequest(Map.of("name", model), "/api/show", false))
        .execute()) {
      return DeserializationUtil.mapResponse(response, OllamaModelInfoResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not get ollama model info for the given model name:\n" + model, e);
    }
  }

  private Request buildCompletionHttpRequest(OllamaCompletionRequest request) {
    return buildPostRequest(request, "/api/generate");
  }

  private Request buildPostRequest(StreamableRequest request, String path) {
    return buildPostRequest(request, path, request.isStream());
  }

  private Request buildPostRequest(Object request, String path, boolean isStream) {
    try {
      return defaultRequest(path, isStream)
          .post(createRequestBody(request))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  private static RequestBody createRequestBody(Object request) throws JsonProcessingException {
    return RequestBody.create(
        new ObjectMapper().writeValueAsString(request),
        MediaType.parse("application/json"));
  }

  private Request.Builder defaultRequest(String path, boolean stream) {
    var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
    return new Request.Builder()
        .url((host == null ? baseHost : host) + path)
        .header("Cache-Control", "no-cache")
        .header("Content-Type", "application/json")
        .header("Accept", stream ? "text/event-stream" : "text/json");
  }

  private CompletionEventSourceListener getCompletionEventSourceListener(
      CompletionEventListener eventListener) {
    return new CompletionEventSourceListener(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          var response = new ObjectMapper().readValue(data, OllamaCompletionResponse.class);
          return response.getResponse();
        } catch (JacksonException e) {
          // ignore
        }
        return "";
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
          return new ObjectMapper().readValue(data, OllamaPullResponse.class);
        } catch (JacksonException e) {
          // ignore
        }
        return null;
      }

      @Override
      protected ErrorDetails getErrorDetails(String error) {
        return new ErrorDetails(error);
      }
    };
  }

  public static class Builder {

    private String host;
    private Integer port;

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setPort(Integer port) {
      this.port = port;
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
