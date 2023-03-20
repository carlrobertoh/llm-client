package ee.carlrobert.openai.client.completion.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.openai.client.completion.CompletionEventListener;
import ee.carlrobert.openai.client.completion.CompletionEventSourceListener;
import ee.carlrobert.openai.client.completion.chat.response.ChatCompletionResponse;
import okhttp3.OkHttpClient;

public class ChatCompletionEventSourceListener extends CompletionEventSourceListener {

  public ChatCompletionEventSourceListener(OkHttpClient client, CompletionEventListener listeners) {
    super(client, listeners);
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
