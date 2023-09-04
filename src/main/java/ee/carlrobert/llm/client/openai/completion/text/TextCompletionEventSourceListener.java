package ee.carlrobert.llm.client.openai.completion.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.completion.ApiResponseError;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.text.response.TextCompletionResponse;
import java.util.function.Consumer;

public class TextCompletionEventSourceListener extends CompletionEventSourceListener {

  public TextCompletionEventSourceListener(CompletionEventListener listeners, boolean retryOnReadTimeout, Consumer<String> onRetry) {
    super(listeners, retryOnReadTimeout, onRetry);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(data, TextCompletionResponse.class)
        .getChoices()
        .get(0)
        .getText();
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return new ObjectMapper().readValue(data, ApiResponseError.class).getError();
  }
}
