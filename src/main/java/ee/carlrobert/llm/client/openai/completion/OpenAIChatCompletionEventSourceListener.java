package ee.carlrobert.llm.client.openai.completion;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener<String> {

  public OpenAIChatCompletionEventSourceListener(CompletionEventListener<String> listener) {
    super(listener);
  }

  /**
   * Content of the first choice.
   * <ul>
   *     <li>Search all choices which are not null</li>
   *     <li>Search all deltas which are not null</li>
   *     <li>Use first content which is not null or blank (whitespace)</li>
   *     <li>Otherwise use "" (empty string) if no match can be found</li>
   * </ul>
   *
   * @return First non-blank content which can be found, otherwise {@code ""}
   */
  protected String getMessage(String data) throws JsonProcessingException {
    var response = OBJECT_MAPPER.readValue(data, OpenAIChatCompletionResponse.class);
    var choices = response.getChoices();

    if (choices == null || choices.isEmpty()) {
      return "";
    }

    var firstChoice = choices.get(0);
    if (firstChoice == null || firstChoice.getDelta() == null) {
      return "";
    }

    var delta = firstChoice.getDelta();
    if (delta.getToolCalls() != null && !delta.getToolCalls().isEmpty()) {
      var toolCalls = delta.getToolCalls();
      if (toolCalls != null && !toolCalls.isEmpty()) {
        for (var toolCall : toolCalls) {
          if (toolCall != null && toolCall.getFunction() != null) {
            listener.onToolCall(toolCall);
          }
        }
      }
    }

    var content = firstChoice.getDelta().getContent();
    if (content == null) {
      return "";
    }

    return content;
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return OBJECT_MAPPER.readValue(data, ApiResponseError.class).getError();
  }
}
