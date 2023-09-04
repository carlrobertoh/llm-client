package ee.carlrobert.llm.client.openai.completion.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.completion.ApiResponseError;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.chat.response.ChatCompletionResponse;
import java.util.function.Consumer;

public class ChatCompletionEventSourceListener extends CompletionEventSourceListener {

  public ChatCompletionEventSourceListener(CompletionEventListener listeners, boolean retryOnReadTimeout, Consumer<String> onRetry) {
    super(listeners, retryOnReadTimeout, onRetry);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    var choice = new ObjectMapper()
        .readValue(data, ChatCompletionResponse.class)
        .getChoices()
        .get(0);
    if (choice != null) {
      var delta = choice.getDelta();
      if (delta != null) {
        return delta.getContent();
      }
    }
    return "";
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return new ObjectMapper().readValue(data, ApiResponseError.class).getError();
  }
}
