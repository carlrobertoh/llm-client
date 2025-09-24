package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.completion.CompletionResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeCompletionStreamResponse implements CompletionResponse {

  private String type;
  private int index;
  private ClaudeCompletionResponseMessage delta;
  @JsonProperty("content_block")
  private ClaudeCompletionResponseMessage contentBlock;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public ClaudeCompletionResponseMessage getDelta() {
    return delta;
  }

  public void setDelta(ClaudeCompletionResponseMessage delta) {
    this.delta = delta;
  }

  public ClaudeCompletionResponseMessage getContentBlock() {
    return contentBlock;
  }

  public void setContentBlock(ClaudeCompletionResponseMessage contentBlock) {
    this.contentBlock = contentBlock;
  }

  public boolean isToolUseContentBlockStart() {
    return "content_block_start".equals(type)
        && contentBlock != null
        && "tool_use".equals(contentBlock.getType());
  }

  public boolean isToolUseDelta() {
    return "content_block_delta".equals(type)
        && delta != null
        && delta.getPartialJson() != null;
  }

  public boolean isContentBlockStop() {
    return "content_block_stop".equals(type);
  }
}
