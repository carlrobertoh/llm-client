package ee.carlrobert.openai.client.completion.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.text.response.TextCompletionResponse;

public class TextCompletionEventSourceListener extends CompletionEventSourceListener {

  public TextCompletionEventSourceListener(CompletionEventListener listeners) {
    super(listeners);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(data, TextCompletionResponse.class)
        .getChoices()
        .get(0)
        .getText();
  }
}
