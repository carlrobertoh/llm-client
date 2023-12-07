package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener {

  public OpenAIChatCompletionEventSourceListener(CompletionEventListener listeners) {
    super(listeners);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    var choice = new ObjectMapper()
        .readValue(data, OpenAIChatCompletionResponse.class)
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
