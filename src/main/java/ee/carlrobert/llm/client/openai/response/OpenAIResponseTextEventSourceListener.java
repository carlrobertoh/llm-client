package ee.carlrobert.llm.client.openai.response;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import ee.carlrobert.llm.client.openai.completion.ApiResponseError;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.response.response.OpenAIResponseCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.util.List;

public class OpenAIResponseTextEventSourceListener extends CompletionEventSourceListener<String> {

  public OpenAIResponseTextEventSourceListener(CompletionEventListener<String> listener) {
    super(listener);
  }

  @Override
  protected String getMessage(String data) throws JsonProcessingException {
    JsonNode root = OBJECT_MAPPER.readTree(data);
    if (root != null && root.has("type")) {
      JsonNode deltaNode = root.get("delta");
      if (deltaNode != null && deltaNode.isTextual()) {
        return deltaNode.asText();
      }

      if (root.has("response") && root.get("response").isObject()) {
        OpenAIResponseCompletionResponse resp =
            OBJECT_MAPPER.treeToValue(root.get("response"), OpenAIResponseCompletionResponse.class);
        return extractFirstTextContent(resp);
      }
      return null;
    }

    OpenAIResponseCompletionResponse resp =
        OBJECT_MAPPER.readValue(data, OpenAIResponseCompletionResponse.class);
    return extractFirstTextContent(resp);
  }

  private String extractFirstTextContent(OpenAIResponseCompletionResponse resp) {
    if (resp == null) {
      return null;
    }
    List<OpenAIResponseCompletionResponse.OutputItem> output = resp.getOutput();
    if (output == null || output.isEmpty()) {
      return null;
    }
    var first = output.get(0);
    if (first == null) {
      return null;
    }
    var content = first.getContent();
    if (content instanceof String) {
      return (String) content;
    }
    try {
      JsonNode node = OBJECT_MAPPER.valueToTree(content);
      if (node.isArray()) {
        StringBuilder sb = new StringBuilder();
        for (JsonNode part : node) {
          if (part.has("type") && "output_text".equals(part.get("type").asText())) {
            JsonNode text = part.get("text");
            if (text != null && text.isTextual()) {
              sb.append(text.asText());
            }
          }
        }
        return sb.length() == 0 ? null : sb.toString();
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    return OBJECT_MAPPER.readValue(data, ApiResponseError.class).getError();
  }
}
