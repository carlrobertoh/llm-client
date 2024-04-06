package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener<String> {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public OpenAIChatCompletionEventSourceListener(CompletionEventListener<String> listeners) {
    super(listeners);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    var choices = OBJECT_MAPPER
        .readValue(data, OpenAIChatCompletionResponse.class)
        .getChoices();
    if (choices != null && !choices.isEmpty()) {
      var choice = choices.get(0);
      if (choice != null) {
        var delta = choice.getDelta();
        if (delta != null) {
          return delta.getContent();
        }
      }
    }

    return "";
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return OBJECT_MAPPER.readValue(data, ApiResponseError.class).getError();
  }
}
