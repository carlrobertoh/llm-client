package ee.carlrobert.llm.client.azure;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class AzureClient {

  private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
  private static final String BASE_URL = PropertiesLoader.getValue("azure.openai.baseUrl");

  private final OkHttpClient httpClient;
  private final String apiKey;
  private final AzureCompletionRequestParams requestParams;
  private final boolean activeDirectoryAuthentication;
  private final String url;

  private AzureClient(Builder builder, OkHttpClient.Builder httpClientBuilder) {
    this.httpClient = httpClientBuilder.build();
    this.apiKey = builder.apiKey;
    this.requestParams = builder.requestParams;
    this.activeDirectoryAuthentication = builder.activeDirectoryAuthentication;
    this.url = String.format(BASE_URL, requestParams.getResourceName());
  }

  public EventSource getChatCompletionAsync(
      OpenAIChatCompletionRequest request,
      CompletionEventListener<String> completionEventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(buildHttpRequest(request), getEventSourceListener(completionEventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAIChatCompletionRequest request) {
    try (var response = httpClient.newCall(buildHttpRequest(request)).execute()) {
      return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Request buildHttpRequest(OpenAIChatCompletionRequest completionRequest) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (completionRequest.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(url + getChatCompletionPath(completionRequest))
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              OBJECT_MAPPER
                  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                  .writeValueAsString(completionRequest),
              APPLICATION_JSON))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getRequiredHeaders() {
    var headers = new HashMap<String, String>();
    headers.put("X-LLM-Application-Tag", "codegpt");
    if (activeDirectoryAuthentication) {
      headers.put("Authorization", "Bearer " + apiKey);
    } else {
      headers.put("api-key", apiKey);
    }
    return headers;
  }

  private String getChatCompletionPath(OpenAIChatCompletionRequest request) {
    return String.format(
        request.getOverriddenPath() == null
            ? "/openai/deployments/%s/chat/completions?api-version=%s"
            : request.getOverriddenPath(),
        requestParams.getDeploymentId(),
        requestParams.getApiVersion());
  }

  private OpenAIChatCompletionEventSourceListener getEventSourceListener(
      CompletionEventListener<String> listener) {
    return new OpenAIChatCompletionEventSourceListener(listener) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(data, AzureApiResponseError.class).getError();
      }
    };
  }

  public static class Builder {

    private final String apiKey;
    private final AzureCompletionRequestParams requestParams;
    private boolean activeDirectoryAuthentication;

    public Builder(String apiKey, AzureCompletionRequestParams requestParams) {
      this.apiKey = apiKey;
      this.requestParams = requestParams;
    }

    public Builder setActiveDirectoryAuthentication(boolean activeDirectoryAuthentication) {
      this.activeDirectoryAuthentication = activeDirectoryAuthentication;
      return this;
    }

    public AzureClient build(OkHttpClient.Builder builder) {
      return new AzureClient(this, builder);
    }

    public AzureClient build() {
      return build(new OkHttpClient.Builder());
    }
  }
}
