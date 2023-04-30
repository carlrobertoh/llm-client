package ee.carlrobert.openai.client.completion.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.completion.ApiResponseError;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.ErrorDetails;
import ee.carlrobert.openai.client.completion.chat.response.ChatCompletionResponse;

public class ChatCompletionEventSourceListener extends CompletionEventSourceListener {

  public ChatCompletionEventSourceListener(CompletionEventListener listeners) {
    super(listeners);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(data, ChatCompletionResponse.class)
        .getChoices()
        .get(0)
        .getDelta()
        .getContent();
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return new ObjectMapper().readValue(data, ApiResponseError.class).getError();
  }
}
