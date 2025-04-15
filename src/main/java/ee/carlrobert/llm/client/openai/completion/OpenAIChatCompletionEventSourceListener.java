package ee.carlrobert.llm.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoice;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;

import java.util.Objects;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener<String> {

  private CompletionDeltaModel prevCompletionDeltaModel = null;

  public OpenAIChatCompletionEventSourceListener(CompletionEventListener<String> listener) {
    super(listener);
  }

  /**
   * Returns the first valid message content extracted from the OpenAI Chat Completion response.
   *
   * <p>This method implements the following new logic:
   * <ul>
   *   <li>Deserializes the provided JSON string into an {@code OpenAIChatCompletionResponse}.</li>
   *   <li>Retrieves the list of choices and filters out any null elements.</li>
   *   <li>For each valid choice, obtains the delta and inspects its fields:
   *       <ul>
   *         <li>If {@code reasoningContent} is available, wraps it into a delta model with only the reasoning set.</li>
   *         <li>Otherwise, wraps the {@code content} field into a delta model.</li>
   *       </ul>
   *   </li>
   *   <li>Chooses the first delta model with a non-null, non-blank content; if none is found, defaults to an empty string.</li>
   *   <li>Formats the output message:
   *       <ul>
   *         <li>Prepends an opening {@code <think>} tag when initiating reasoning content.</li>
   *         <li>Appends a closing {@code </think>} tag and new line characters if transitioning from reasoning to normal content.</li>
   *       </ul>
   *   </li>
   * </ul>
   *
   * @param data the JSON string received from the OpenAI Chat Completion API.
   * @return the formatted message extracted from the first valid delta model or an empty string if no matching content is found.
   * @throws JsonProcessingException if an error occurs during JSON processing.
   */
  protected String getMessage(String data) throws JsonProcessingException {
    var response = OBJECT_MAPPER.readValue(data, OpenAIChatCompletionResponse.class);
    var choices = response.getChoices();

    CompletionDeltaModel completionDeltaModel = choices == null ? new CompletionDeltaModel(null, "") : choices.stream()
            .filter(Objects::nonNull)
            .map(OpenAIChatCompletionResponseChoice::getDelta)
            .filter(Objects::nonNull)
            .map(delta ->
                    delta.getReasoningContent() != null
                            ? new CompletionDeltaModel(delta.getReasoningContent(), null)
                            : new CompletionDeltaModel(null, delta.getContent())
            )
            .findFirst()
            .orElse(new CompletionDeltaModel(null, ""));

    return getMessageFromDeltaModel(completionDeltaModel);
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return OBJECT_MAPPER.readValue(data, ApiResponseError.class).getError();
  }

  private String getMessageFromDeltaModel(CompletionDeltaModel completionDeltaModel) {
    StringBuilder sb = new StringBuilder();
    String reasoning = completionDeltaModel.getReasoningContent();
    String content = completionDeltaModel.getContent();

    if (reasoning != null) {
      if (prevCompletionDeltaModel == null || prevCompletionDeltaModel.getReasoningContent() == null) {
        sb.append("<think>");
      }
      sb.append(reasoning);
    } else if (content != null) {
      if (prevCompletionDeltaModel != null && prevCompletionDeltaModel.getReasoningContent() != null) {
        sb.append("</think>\n\n");
      }
      sb.append(content);
    }

    prevCompletionDeltaModel = completionDeltaModel;
    return sb.toString();
  }
}
