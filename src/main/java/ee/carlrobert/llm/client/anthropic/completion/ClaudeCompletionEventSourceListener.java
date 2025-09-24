package ee.carlrobert.llm.client.anthropic.completion;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.response.ToolCall;
import ee.carlrobert.llm.client.openai.completion.response.ToolFunctionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event source listener for Claude completion streams that handles tool calls and thinking mode.
 * This listener processes streaming responses from the Claude API and manages tool call state.
 */
public class ClaudeCompletionEventSourceListener extends CompletionEventSourceListener<String> {

  private final Map<Integer, ClaudeToolCallBuilder> activeToolCalls = new ConcurrentHashMap<>();

  public ClaudeCompletionEventSourceListener(CompletionEventListener<String> listener) {
    super(listener);
  }

  @Override
  protected String getMessage(String data) throws JsonProcessingException {
    try {
      var response = OBJECT_MAPPER.readValue(data, ClaudeCompletionStreamResponse.class);
      return processStreamResponse(response);
    } catch (Exception e) {
      return handleErrorResponse(data);
    }
  }

  private String processStreamResponse(ClaudeCompletionStreamResponse response) {
    if (response.isToolUseContentBlockStart()) {
      handleToolUseStart(response);
      return "";
    }

    if (response.isToolUseDelta()) {
      handleToolUseDelta(response);
      return "";
    }

    if (response.isContentBlockStop()) {
      return handleContentBlockStop(response);
    }

    if (response.getDelta() != null) {
      return processDeltaContent(response.getDelta());
    }

    return "";
  }

  private String processDeltaContent(ClaudeCompletionResponseMessage delta) {
    if (delta.getThinking() != null) {
      listener.onThinking(delta.getThinking());
      return "";
    }

    if (delta.isTextDelta()) {
      return delta.getText() != null ? delta.getText() : "";
    }

    return delta.getText() != null ? delta.getText() : "";
  }

  private String handleErrorResponse(String data) {
    try {
      var errorResponse = OBJECT_MAPPER.readValue(data, ClaudeCompletionErrorDetails.class);
      return errorResponse.getError().getMessage();
    } catch (Exception ex) {
      return "";
    }
  }

  private void handleToolUseStart(ClaudeCompletionStreamResponse response) {
    var contentBlock = response.getContentBlock();
    if (contentBlock != null) {
      var builder = new ClaudeToolCallBuilder(
          contentBlock.getId(),
          contentBlock.getName(),
          response.getIndex()
      );
      activeToolCalls.put(response.getIndex(), builder);
    }
  }

  private void handleToolUseDelta(ClaudeCompletionStreamResponse response) {
    var delta = response.getDelta();
    var builder = activeToolCalls.get(response.getIndex());

    if (builder != null && delta != null && delta.getPartialJson() != null) {
      builder.appendPartialJson(delta.getPartialJson());
    }
  }

  private String handleContentBlockStop(ClaudeCompletionStreamResponse response) {
    var builder = activeToolCalls.remove(response.getIndex());
    if (builder != null && builder.isToolCall()) {
      listener.onToolCall(builder.buildToolCall());
    }

    return "";
  }

  @Override
  protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
    var errorResponse = OBJECT_MAPPER.readValue(data, ClaudeCompletionErrorDetails.class);
    return errorResponse.getError();
  }

  private static class ClaudeToolCallBuilder {

    private final String id;
    private final String name;
    private final int index;
    private final StringBuilder partialJsonBuilder = new StringBuilder();

    public ClaudeToolCallBuilder(String id, String name, int index) {
      this.id = id;
      this.name = name;
      this.index = index;
    }

    public void appendPartialJson(String partialJson) {
      if (partialJson != null) {
        partialJsonBuilder.append(partialJson);
      }
    }

    public boolean isToolCall() {
      return id != null && name != null;
    }

    public ToolCall buildToolCall() {
      var arguments = partialJsonBuilder.toString();
      if (arguments.isEmpty()) {
        arguments = "{}";
      }

      return new ToolCall(
          index,
          id,
          "function",
          new ToolFunctionResponse(name, arguments)
      );
    }
  }
}