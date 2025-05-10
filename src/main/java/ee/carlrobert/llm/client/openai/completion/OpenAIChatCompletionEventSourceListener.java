package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoice;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.util.Objects;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener<ChatCompletionResponseData> {


  public OpenAIChatCompletionEventSourceListener(CompletionEventListener<ChatCompletionResponseData> listener) {
    super(listener);
  }

  /**
   * Returns the first valid message content extracted from the OpenAI Chat Completion response.
   *
   * @param data the JSON string received from the OpenAI Chat Completion API.
   * @return ChatCompletionResponseData object containing content and reasoningContent
   * @throws JsonProcessingException if an error occurs during JSON processing.
   */
  protected ChatCompletionResponseData getMessage(String data) throws JsonProcessingException {
    var response = OBJECT_MAPPER.readValue(data, OpenAIChatCompletionResponse.class);
    var choices = response.getChoices();

    return choices == null ? new ChatCompletionResponseData(null, null) : choices.stream()
            .filter(Objects::nonNull)
            .map(OpenAIChatCompletionResponseChoice::getDelta)
            .filter(Objects::nonNull)
            .map(delta -> new ChatCompletionResponseData(delta.getContent(), delta.getReasoningContent()))
            .findFirst()
            .orElse(new ChatCompletionResponseData(null, null));
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return OBJECT_MAPPER.readValue(data, ApiResponseError.class).getError();
  }
}
