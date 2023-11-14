package ee.carlrobert.llm.client.llama;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class LlamaClient extends Client {

  private static final String BASE_URL = PropertiesLoader.getValue("llama.baseUrl");
  private final Integer port;

  protected LlamaClient(Builder builder) {
    super(builder);
    this.port = builder.port;
  }

  public EventSource getChatCompletion(
      LlamaCompletionRequest request, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(getHttpClient())
        .newEventSource(buildHttpRequest(request), getEventSourceListener(completionEventListener));
  }

  private Request buildHttpRequest(LlamaCompletionRequest request) {
    try {
      var baseHost = port == null ? BASE_URL : format("http://localhost:%d", port);
      return new Request.Builder()
          .url(getHost() != null ? getHost() : baseHost + "/completion")
          .header("Accept", "text/event-stream")
          .header("Cache-Control", "no-cache")
          .post(RequestBody.create(
              new ObjectMapper().writeValueAsString(request),
              MediaType.parse("application/json")))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private CompletionEventSourceListener getEventSourceListener(
      CompletionEventListener eventListener) {
    return new CompletionEventSourceListener(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          var response = new ObjectMapper().readValue(data, LlamaCompletionResponse.class);
          return response.getContent();
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

  public static class Builder extends Client.Builder {

    private Integer port;

    public Builder setPort(Integer port) {
      this.port = port;
      return this;
    }

    public LlamaClient build() {
      return new LlamaClient(this);
    }
  }
}
