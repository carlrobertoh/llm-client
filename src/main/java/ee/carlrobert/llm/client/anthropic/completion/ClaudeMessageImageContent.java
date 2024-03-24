package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Messages with image content are supported starting with Claude 3 models.
 */
@JsonTypeName("image")
public class ClaudeMessageImageContent extends ClaudeMessageContent{

  private ClaudeSource source;

  public ClaudeMessageImageContent() {
  }

  public ClaudeSource getSource() {
    return source;
  }

  public ClaudeMessageImageContent(ClaudeSource source) {
    this.source = source;
  }
}
