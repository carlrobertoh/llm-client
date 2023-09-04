package ee.carlrobert.llm.client.you.completion;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.completion.CompletionClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.YouClient;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class YouCompletionClient extends CompletionClient {

  private final YouClient client;

  public YouCompletionClient(YouClient client) {
    super(client);
    this.client = client;
  }

  public EventSource stream(Request request, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(
            setCookie(request),
            getEventListener(completionEventListener, client.isRetryOnReadTimeout(), (response) -> {}));
  }

  private Request setCookie(Request request) {
    return request.newBuilder()
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
        .build();
  }

  @Override
  protected Map<String, String> getRequiredHeaders() {
    return null;
  }

  @Override
  protected CompletionEventSourceListener getEventListener(CompletionEventListener listeners, boolean retryOnReadTimeout, Consumer<String> onRetry) {
    return new CompletionEventSourceListener(listeners, retryOnReadTimeout, onRetry) {
      @Override
      protected String getMessage(String data) {
        try {
          return new ObjectMapper().readValue(data, YouCompletionResponse.class).getYouChatToken();
        } catch (JacksonException e) {
          // ignore
        }
        return "";
      }

      @Override
      protected ErrorDetails getErrorDetails(String data) {
        return null;
      }
    };
  }
}
