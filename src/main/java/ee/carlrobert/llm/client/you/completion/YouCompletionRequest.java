package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.PropertiesLoader;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class YouCompletionRequest {

  private static final String host = PropertiesLoader.getValue("you.url.host");
  private static final String scheme = PropertiesLoader.getValue("you.url.scheme");
  private static final String port = PropertiesLoader.getValue("you.url.port");

  private final String prompt;
  private final List<YouCompletionRequestMessage> messages;

  public YouCompletionRequest(Builder builder) {
    this.prompt = builder.prompt;
    this.messages = builder.messages;
  }

  public Request toHttoRequest() {
    try {
      var httpUrlBuilder = new HttpUrl.Builder()
          // .scheme("https")
          .scheme(scheme == null ? "https" : scheme)
          .host(host)
          .addPathSegments("api/streamingSearch")
          .addQueryParameter("q", prompt)
          .addQueryParameter("page", "1")
          .addQueryParameter("count", "10")
          .addQueryParameter("safeSearch", "WebPages,Translations,TimeZone,Computation,RelatedSearches")
          .addQueryParameter("domain", "youchat")
          .addQueryParameter("queryTraceId", "88ccf267-104c-413d-88ad-68fa859a004e")
          .addQueryParameter("chat", new ObjectMapper().writeValueAsString(messages))
          .addQueryParameter("chatId", "88ccf267-104c-413d-88ad-68fa859a004e");

      if (port != null && !port.isEmpty()) {
        httpUrlBuilder.port(Integer.parseInt(port));
      }

      return new Request.Builder()
          .url(httpUrlBuilder.build())
          .header("Accept", "text/event-stream")
          .header("Cache-Control", "no-cache")
          .get()
          .build();
    } catch (Throwable e) { // JsonProcessingException
      throw new RuntimeException("Could not build http request", e);
    }
  }

  public static class Builder {

    private final String prompt;
    private List<YouCompletionRequestMessage> messages;

    public Builder(String prompt) {
      this.prompt = prompt;
    }

    public Builder setChatHistory(List<YouCompletionRequestMessage> messages) {
      this.messages = messages;
      return this;
    }

    public YouCompletionRequest buildRequest() {
      return new YouCompletionRequest(this);
    }
  }
}
