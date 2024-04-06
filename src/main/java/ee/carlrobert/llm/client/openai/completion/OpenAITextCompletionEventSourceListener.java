package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.completion.response.OpenAITextCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;

public class OpenAITextCompletionEventSourceListener extends CompletionEventSourceListener {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public OpenAITextCompletionEventSourceListener(CompletionEventListener listeners) {
    super(listeners);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    var choice = OBJECT_MAPPER
        .readValue(data, OpenAITextCompletionResponse.class)
        .getChoices()
        .get(0);
    if (choice != null) {
      return choice.getText();
    }
    return "";
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return OBJECT_MAPPER.readValue(data, ApiResponseError.class).getError();
  }
}
