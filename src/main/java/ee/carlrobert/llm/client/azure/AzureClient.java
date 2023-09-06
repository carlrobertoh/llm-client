package ee.carlrobert.llm.client.azure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.chat.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class AzureClient extends Client {

  private static final String BASE_URL = PropertiesLoader.getValue("azure.openai.baseUrl");

  private final AzureCompletionRequestParams requestParams;
  private final boolean activeDirectoryAuthentication;
  private final String url;

  private AzureClient(Builder builder) {
    super(builder);
    this.requestParams = builder.requestParams;
    this.activeDirectoryAuthentication = builder.activeDirectoryAuthentication;
    this.url = String.format(getHost() == null ? BASE_URL : getHost(), builder.requestParams);
  }

  public EventSource getChatCompletion(OpenAICompletionRequest request, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(getHttpClient()).newEventSource(
        buildHttpRequest(request, getChatCompletionPath()),
        getEventSourceListener(completionEventListener));
  }

  public OpenAIChatCompletionResponse getChatCompletion(OpenAICompletionRequest request) {
    try (var response = getHttpClient()
        .newCall(buildHttpRequest(request, getChatCompletionPath()))
        .execute()) {
      return new ObjectMapper().readValue(Objects.requireNonNull(response.body()).string(), OpenAIChatCompletionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Request buildHttpRequest(OpenAICompletionRequest completionRequest, String path) {
    var headers = new HashMap<>(getRequiredHeaders());
    if (completionRequest.isStream()) {
      headers.put("Accept", "text/event-stream");
    }
    try {
      return new Request.Builder()
          .url(url + path)
          .headers(Headers.of(headers))
          .post(RequestBody.create(
              new ObjectMapper().writeValueAsString(completionRequest),
              MediaType.parse("application/json")))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to process request", e);
    }
  }

  private Map<String, String> getRequiredHeaders() {
    return activeDirectoryAuthentication ?
        Map.of("Authorization", "Bearer " + getApiKey()) :
        Map.of("api-key", getApiKey());
  }

  private String getChatCompletionPath() {
    return String.format("/openai/deployments/%s/chat/completions?api-version=%s", requestParams.getDeploymentId(), requestParams.getApiVersion());
  }

  private OpenAIChatCompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners) {
    return new OpenAIChatCompletionEventSourceListener(listeners) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return new ObjectMapper().readValue(data, AzureApiResponseError.class).getError();
      }
    };
  }

  public static class Builder extends Client.Builder {

    private final AzureCompletionRequestParams requestParams;
    private boolean activeDirectoryAuthentication;

    public Builder(String apiKey, AzureCompletionRequestParams requestParams) {
      super(apiKey);
      this.requestParams = requestParams;
    }

    public Builder setActiveDirectoryAuthentication(boolean activeDirectoryAuthentication) {
      this.activeDirectoryAuthentication = activeDirectoryAuthentication;
      return this;
    }

    @Override
    public AzureClient build() {
      return new AzureClient(this);
    }
  }
}
