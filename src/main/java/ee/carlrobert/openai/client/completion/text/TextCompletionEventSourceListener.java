package ee.carlrobert.openai.client.completion.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.BaseApiResponseError;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.text.response.TextCompletionResponse;

public class TextCompletionEventSourceListener<E extends BaseApiResponseError>
    extends CompletionEventSourceListener<E> {

  public TextCompletionEventSourceListener(CompletionEventListener listeners, Class<E> errorType) {
    super(listeners, errorType);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(data, TextCompletionResponse.class)
        .getChoices()
        .get(0)
        .getText();
  }
}
