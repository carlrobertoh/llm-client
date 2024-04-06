package ee.carlrobert.llm.client.openai.completion;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener<String> {

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
