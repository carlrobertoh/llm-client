package ee.carlrobert.llm.client.openai.completion;

public interface BaseApiResponseError {

  ErrorDetails getError();
}
