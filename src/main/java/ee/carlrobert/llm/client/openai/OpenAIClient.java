package ee.carlrobert.llm.client.openai;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException;
import ee.carlrobert.llm.client.openai.completion.ApiResponseError;
import ee.carlrobert.llm.client.openai.completion.ChatCompletionResponseData;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.embeddings.EmbeddingData;
import ee.carlrobert.llm.client.openai.embeddings.EmbeddingRequest;
import ee.carlrobert.llm.client.openai.embeddings.EmbeddingResponse;
import ee.carlrobert.llm.client.openai.imagegen.request.OpenAIImageGenerationRequest;
import ee.carlrobert.llm.client.openai.imagegen.response.OpenAiImageGenerationResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class OpenAIClient {

  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
  private final OkHttpClient httpClient;
  private final String apiKey;
  private final String organization;
  private final String host;
  private final String pluginVersion;

  private OpenAIClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.apiKey = builder.apiKey;
    this.organization = builder.organization;
    this.host = builder.host;
    this.pluginVersion = builder.pluginVersion;
  }

  public EventSource getCompletionAsync(
      OpenAITextCompletionRequest request,
      CompletionEventListener<String> eventListener) {
    return getCompletionAsync(request, new OpenAITextCompletionEventSourceListener(eventListener));
  }

  public EventSource getCompletionAsync(
      OpenAITextCompletionRequest request,
      OpenAITextCompletionEventSourceListener eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(buildTextCompletionRequest(request), eventListener);
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<ChatCompletionResponseData> eventListener) {
    return getChatCompletionAsync(
        request,
        new OpenAIChatCompletionEventSourceListener(eventListener));
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      OpenAIChatCompletionEventSourceListener eventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(buildChatCompletionRequest(request), eventListener);
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAIChatCompletionRequest request) {
    try (var response = httpClient.newCall(buildChatCompletionRequest(request)).execute()) {
      if (!response.isSuccessful()) {
        var body = response.body();
        if (body == null) {
          throw new RuntimeException("Unable to get response body");
        }

        var error = OBJECT_MAPPER.readValue(body.string(), ApiResponseError.class);
        var ex = new CodeGPTException();
        ex.setDetail(error.getError().getMessage());
        ex.setStatus(response.code());
        throw ex;
      }

      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public OpenAiImageGenerationResponse getImage(OpenAIImageGenerationRequest request) {
    try (var response = httpClient.newBuilder()
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS).build().newCall(buildImageRequest(request))
        .execute()) {
      return DeserializationUtil.mapResponse(response, OpenAiImageGenerationResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * First non-null embedding response (or null).
   *
   * @param input Request texts
   * @return First non-null embedding response (if there is one)
   */
  public double[] getEmbedding(String input) {
    var embeddings = getEmbeddings(new EmbeddingRequest("text-embedding-3-large", List.of(input)));
    return embeddings.isEmpty() ? null : embeddings.get(0);
  }

  /**
   * Embeddings response (empty list if none could be found).
   *
   * @param request Embedding request
   * @return Non-null response embeddings
   */
  public List<double[]> getEmbeddings(EmbeddingRequest request) {
    try (var response = httpClient
        .newCall(buildEmbeddingsRequest(host + "/v1/embeddings", request))
        .execute()) {

      return Optional.ofNullable(DeserializationUtil.mapResponse(response, EmbeddingResponse.class))
          .map(EmbeddingResponse::getData)
          .stream()
          .flatMap(Collection::stream)
          .filter(Objects::nonNull)
          .map(EmbeddingData::getEmbedding)
          .filter(Objects::nonNull)
          .collect(toList());
    } catch (IOException e) {
      throw new RuntimeException("Unable to fetch embedding", e);
    }
  }

  private Request buildEmbeddingsRequest(String url, EmbeddingRequest request)
      throws JsonProcessingException {
    return new Request.Builder()
        .url(url)
        .headers(Headers.of(getHeaders()))
        .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
        .build();
  }

  public Request buildImageRequest(OpenAIImageGenerationRequest imageRequest) {
    var headers = new HashMap<>(getHeaders());
    headers.put("Content-Type", "application/json");
    try {
      var overriddenPath = imageRequest.getOverriddenPath();
      return new Request.Builder()
          .url(host + (overriddenPath == null ? "/v1/images/generations" : overriddenPath))
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              OBJECT_MAPPER
                  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                  .writeValueAsString(imageRequest),
              APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Request buildChatCompletionRequest(OpenAIChatCompletionRequest request) {
    var headers = new HashMap<>(getHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      var overriddenPath = request.getOverriddenPath();
      return new Request.Builder()
          .url(host + (overriddenPath == null ? "/v1/chat/completions" : overriddenPath))
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Request buildTextCompletionRequest(OpenAITextCompletionRequest request) {
    var headers = new HashMap<>(getHeaders());
    if (request.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(host + "/v1/completions")
          .headers(Headers.of(headers))
          .post(RequestBody.create(OBJECT_MAPPER.writeValueAsString(request), APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getHeaders() {
    var headers = new HashMap<>(Map.of("X-LLM-Application-Tag", "codegpt"));
    if (apiKey != null && !apiKey.isEmpty()) {
      headers.put("Authorization", "Bearer " + apiKey);
    }
    if (pluginVersion != null && !pluginVersion.isEmpty()) {
      headers.put("X-Plugin-Version", pluginVersion);
    }
    if (organization != null && !organization.isEmpty()) {
      headers.put("OpenAI-Organization", organization);
    }
    return headers;
  }

  public static class Builder {

    private final String apiKey;
    private String host = PropertiesLoader.getValue("openai.baseUrl");
    private String organization;
    private String pluginVersion;

    public Builder(String apiKey) {
      this.apiKey = apiKey;
    }

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setOrganization(String organization) {
      this.organization = organization;
      return this;
    }

    public Builder setPluginVersion(String pluginVersion) {
      this.pluginVersion = pluginVersion;
      return this;
    }

    public OpenAIClient build(OkHttpClient.Builder builder) {
      return new OpenAIClient(this, builder);
    }

    public OpenAIClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
