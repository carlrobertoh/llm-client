package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoice;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoiceDelta;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.util.Objects;
import java.util.stream.Stream;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener<String> {

  public OpenAIChatCompletionEventSourceListener(CompletionEventListener<String> listeners) {
    super(listeners);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    var choices = new ObjectMapper()
        .readValue(data, OpenAIChatCompletionResponse.class)
        .getChoices();
    return (choices == null ? Stream.<OpenAIChatCompletionResponseChoice>empty() : choices.stream())
            .filter(Objects::nonNull)
            .map(OpenAIChatCompletionResponseChoice::getDelta)
            .filter(Objects::nonNull)
            .map(OpenAIChatCompletionResponseChoiceDelta::getContent)
            .filter(c -> c != null && !c.isBlank())
            .findFirst()
            .orElse("");
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return new ObjectMapper().readValue(data, ApiResponseError.class).getError();
  }
}
