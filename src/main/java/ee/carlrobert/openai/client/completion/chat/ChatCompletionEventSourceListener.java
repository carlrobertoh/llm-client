package ee.carlrobert.openai.client.completion.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.completion.BaseApiResponseError;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.chat.response.ChatCompletionResponse;

public class ChatCompletionEventSourceListener<E extends BaseApiResponseError>
    extends CompletionEventSourceListener<E> {

  public ChatCompletionEventSourceListener(CompletionEventListener listeners, Class<E> errorType) {
    super(listeners, errorType);
  }

  protected String getMessage(String data) throws JsonProcessingException {
    return new ObjectMapper()
        .readValue(data, ChatCompletionResponse.class)
        .getChoices()
        .get(0)
        .getDelta()
        .getContent();
  }
}
