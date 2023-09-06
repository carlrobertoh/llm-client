package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.YouClient;
import ee.carlrobert.llm.completion.CompletionClient;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class YouCompletionClient extends CompletionClient {

  private static final String host = PropertiesLoader.getValue("you.url.host");
  private static final String scheme = PropertiesLoader.getValue("you.url.scheme");
  private static final String port = PropertiesLoader.getValue("you.url.port");

  private final YouClient client;

  public YouCompletionClient(YouClient client) {
    super(client);
    this.client = client;
  }

  @Override
  public EventSource getCompletion(CompletionRequest request, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(buildHttpRequest((YouCompletionRequest) request), getEventSourceListener(completionEventListener));
  }

  @Override
  public YouCompletionResponse getCompletion(CompletionRequest request) {
    throw new RuntimeException("Not implemented!");
  }

  public Request buildHttpRequest(YouCompletionRequest request) {
    try {
      var httpUrlBuilder = new HttpUrl.Builder()
          // .scheme("https")
          .scheme(scheme == null ? "https" : scheme)
          .host(host)
          .addPathSegments("api/streamingSearch")
          .addQueryParameter("q", request.getPrompt())
          .addQueryParameter("page", "1")
          .addQueryParameter("count", "10")
          .addQueryParameter("safeSearch", "WebPages,Translations,TimeZone,Computation,RelatedSearches")
          .addQueryParameter("domain", "youchat")
          .addQueryParameter("queryTraceId", request.getQueryTraceId().toString())
          .addQueryParameter("chat", new ObjectMapper().writeValueAsString(request.getMessages()))
          .addQueryParameter("chatId", request.getChatId().toString());

      if (port != null && !port.isEmpty()) {
        httpUrlBuilder.port(Integer.parseInt(port));
      }

      return new Request.Builder()
          .url(httpUrlBuilder.build())
          .header("Accept", "text/event-stream")
          .header("Cache-Control", "no-cache")
          .header("Cookie", (
              "uuid_guest=f9e7e074-54e1-43d9-a12d-30900b066d0c; " + // TODO
                  "safesearch_guest=Moderate; " +
                  "youpro_subscription=true; " +
                  "you_subscription=free; " +
                  "stytch_session=" + client.getSessionId() + "; " +
                  "ydc_stytch_session=" + client.getSessionId() + "; " +
                  "stytch_session_jwt=" + client.getAccessToken() + "; " +
                  "ydc_stytch_session_jwt=" + client.getAccessToken() + "; " +
                  "safesearch_9015f218b47611b62bbbaf61125cd2dac629e65c3d6f47573a2ec0e9b615c691=Moderate; " +
                  "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-AWIt5Mr4Y3xQI4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8SVMvBZPI3zkOgcRs7Mz3yazd7f7c58HwW5Xg9jdBjNg;"))
          .get()
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not build http request", e);
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
}
