package ee.carlrobert.llm.client.you;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.Client;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouCompletionEventListener;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequest;
import ee.carlrobert.llm.client.you.completion.YouCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;


public class YouClient extends Client {

  private static final String host = PropertiesLoader.getValue("you.url.host");
  private static final String scheme = PropertiesLoader.getValue("you.url.scheme");
  private static final String port = PropertiesLoader.getValue("you.url.port");

  private final String sessionId;
  private final String accessToken;

  private YouClient(YouClient.Builder builder) {
    super(builder);
    this.sessionId = builder.sessionId;
    this.accessToken = builder.accessToken;
  }

  public EventSource getChatCompletion(YouCompletionRequest request, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(getHttpClient())
        .newEventSource(buildHttpRequest(request), getEventSourceListener(completionEventListener));
  }

  private Request buildHttpRequest(YouCompletionRequest request) {
    return new Request.Builder()
        .url(buildHttpUrl(request))
        .header("Accept", "text/event-stream")
        .header("Cache-Control", "no-cache")
        .header("User-Agent", "youide CodeGPT")
        .header("Cookie", (
            "uuid_guest=" + request.getChatId().toString() != null ? request.getChatId().toString() : "-54e1-43d9-a12d-30900b066d0c" + "; " +
                "safesearch_guest=Moderate; " +
                "youpro_subscription=true; " +
                "you_subscription=free; " +
                "stytch_session=" + sessionId + "; " +
                "ydc_stytch_session=" + sessionId + "; " +
                "stytch_session_jwt=" + accessToken + "; " +
                "ydc_stytch_session_jwt=" + accessToken + "; " +
                "eg4=" + request.isUseGPT4Model() + ";" +
                "safesearch_9015f218b47611b62bbbaf61125cd2dac629e65c3d6f47573a2ec0e9b615c691=Moderate; " +
                "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-AWIt5Mr4Y3xQI4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8SVMvBZPI3zkOgcRs7Mz3yazd7f7c58HwW5Xg9jdBjNg;"))
        .get()
        .build();
  }

  private HttpUrl buildHttpUrl(YouCompletionRequest request) {
    try {
      var httpUrlBuilder = new HttpUrl.Builder()
          // .scheme("https")
          .scheme(scheme == null ? "https" : scheme)
          .host(host)
          .addPathSegments("api/streamingSearch")
          .addQueryParameter("q", request.getPrompt())
          .addQueryParameter("page", "1")
              .addQueryParameter("utm_source", "ide")
              .addQueryParameter("utm_medium", "jetbrains")
              .addQueryParameter("utm_campaign", "0.0.6")
              .addQueryParameter("utm_content", "CodeGPT")
              .addQueryParameter("cfr", "CodeGPT")
          .addQueryParameter("count", "10")
          .addQueryParameter("safeSearch", "WebPages,Translations,TimeZone,Computation,RelatedSearches")
          .addQueryParameter("domain", "youchat")
          .addQueryParameter("chat", new ObjectMapper().writeValueAsString(request.getMessages()));

      if (request.getChatId() != null) {
        httpUrlBuilder.addQueryParameter("chatId", request.getChatId().toString());
      }
      if (request.getQueryTraceId() != null) {
        httpUrlBuilder.addQueryParameter("queryTraceId", request.getQueryTraceId().toString());
      }
      if (port != null && !port.isEmpty()) {
        httpUrlBuilder.port(Integer.parseInt(port));
      }
      return httpUrlBuilder.build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize request messages", e);
    }
  }

  private CompletionEventSourceListener getEventSourceListener(CompletionEventListener eventListener) {
    return new CompletionEventSourceListener(eventListener) {
      @Override
      protected String getMessage(String data) {
        try {
          var response = new ObjectMapper().readValue(data, YouCompletionResponse.class);
          if (eventListener instanceof YouCompletionEventListener) {
            var serpResults = response.getSerpResults();
            if (serpResults != null) {
              ((YouCompletionEventListener) eventListener).onSerpResults(serpResults);
            }
          }

          return response.getChatToken();
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

    private final String sessionId;
    private final String accessToken;

    public Builder(String sessionId, String accessToken) {
      this.sessionId = sessionId;
      this.accessToken = accessToken;
    }

    public YouClient build() {
      return new YouClient(this);
    }
  }
}
