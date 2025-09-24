package ee.carlrobert.llm.client.anthropic.completion;

/**
 * Exception thrown when there are issues with Claude completion requests or responses.
 * This exception provides more specific error information than generic RuntimeException.
 */
public class ClaudeCompletionException extends RuntimeException {

  /**
   * Creates a new ClaudeCompletionException with the specified message.
   *
   * @param message The error message
   */
  public ClaudeCompletionException(String message) {
    super(message);
  }

  /**
   * Creates a new ClaudeCompletionException with the specified message and cause.
   *
   * @param message The error message
   * @param cause The underlying cause of the exception
   */
  public ClaudeCompletionException(String message, Throwable cause) {
    super(message, cause);
  }
}