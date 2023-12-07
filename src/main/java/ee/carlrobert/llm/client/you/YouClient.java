package ee.carlrobert.llm.client.you;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouCompletionEventListener;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequest;
import ee.carlrobert.llm.client.you.completion.YouCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class YouClient {

  private static final String BASE_HOST = PropertiesLoader.getValue("you.baseUrl");

  private final String sessionId;
  private final String accessToken;
  private final UTMParameters utmParameters;
  private final OkHttpClient httpClient;

  private YouClient(YouClient.Builder builder) {
    this.httpClient = new OkHttpClient.Builder().build();
    this.sessionId = builder.sessionId;
    this.accessToken = builder.accessToken;
    this.utmParameters = builder.utmParameters;
  }

  public EventSource getChatCompletion(
      YouCompletionRequest request,
      CompletionEventListener completionEventListener) {
    return EventSources.createFactory(httpClient)
        .newEventSource(buildHttpRequest(request), getEventSourceListener(completionEventListener));
  }

  private Request buildHttpRequest(YouCompletionRequest request) {
    var guestIdCookie = request.getUserId() != null ?
        ("uuid_guest=" + request.getUserId().toString() + "; ")
        : "";
    return new Request.Builder()
        .url(buildHttpUrl(request))
        .header("Accept", "text/event-stream")
        .header("Cache-Control", "no-cache")
        .header("User-Agent", "youide CodeGPT")
        .header("Cookie", (
            guestIdCookie +
                "safesearch_guest=Moderate; " +
                "youpro_subscription=true; " +
                "you_subscription=free; " +
                "stytch_session=" + sessionId + "; " +
                "ydc_stytch_session=" + sessionId + "; " +
                "stytch_session_jwt=" + accessToken + "; " +
                "ydc_stytch_session_jwt=" + accessToken + "; " +
                "eg4=" + request.isUseGPT4Model() + "; " +
                "safesearch_9015f218b47611b62bbbaf61125cd2dac629e65c3d6f47573a2ec0e9b615c691=Moderate; " +
                "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-AWIt5Mr4Y3xQI4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8SVMvBZPI3zkOgcRs7Mz3yazd7f7c58HwW5Xg9jdBjNg;"))
        .get()
        .build();
  }

  private HttpUrl buildHttpUrl(YouCompletionRequest request) {
    try {
      var url = new URL(BASE_HOST);
      var httpUrlBuilder = new HttpUrl.Builder()
          .scheme(url.getProtocol())
          .host(url.getHost())
          .addPathSegments("api/streamingSearch")
          .addQueryParameter("q", request.getPrompt())
          .addQueryParameter("page", "1")
          .addQueryParameter("cfr", "CodeGPT")
          .addQueryParameter("count", "10")
          .addQueryParameter(
              "safeSearch",
              "WebPages,Translations,TimeZone,Computation,RelatedSearches")
          .addQueryParameter("domain", "youchat")
          .addQueryParameter("chat", new ObjectMapper().writeValueAsString(request.getMessages()));

      if (url.getPort() != -1) {
        httpUrlBuilder.port(url.getPort());
      }
      if (request.getChatId() != null) {
        httpUrlBuilder.addQueryParameter("chatId", request.getChatId().toString());
      }
      if (request.getQueryTraceId() != null) {
        httpUrlBuilder.addQueryParameter("queryTraceId", request.getQueryTraceId().toString());
      }
      if (utmParameters != null) {
        addUTMParameters(httpUrlBuilder);
      }
      return httpUrlBuilder.build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize request messages", e);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid url", e);
    }
  }

  private void addUTMParameters(HttpUrl.Builder httpUrlBuilder) {
    if (utmParameters.getId() != null) {
      httpUrlBuilder.addQueryParameter("utm_id", utmParameters.getId());
    }
    if (utmParameters.getSource() != null) {
      httpUrlBuilder.addQueryParameter("utm_source", utmParameters.getSource());
    }
    if (utmParameters.getMedium() != null) {
      httpUrlBuilder.addQueryParameter("utm_medium", utmParameters.getMedium());
    }
    if (utmParameters.getCampaign() != null) {
      httpUrlBuilder.addQueryParameter("utm_campaign", utmParameters.getCampaign());
    }
    if (utmParameters.getContent() != null) {
      httpUrlBuilder.addQueryParameter("utm_content", utmParameters.getContent());
    }
    if (utmParameters.getTerm() != null) {
      httpUrlBuilder.addQueryParameter("utm_term", utmParameters.getTerm());
    }
  }

  private CompletionEventSourceListener getEventSourceListener(
      CompletionEventListener eventListener) {
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

  public static class Builder {

    private final String sessionId;
    private final String accessToken;
    private UTMParameters utmParameters;

    public Builder(String sessionId, String accessToken) {
      this.sessionId = sessionId;
      this.accessToken = accessToken;
    }

    public Builder setUTMParameters(UTMParameters utmParameters) {
      this.utmParameters = utmParameters;
      return this;
    }

    public YouClient build() {
      return new YouClient(this);
    }
  }
}
