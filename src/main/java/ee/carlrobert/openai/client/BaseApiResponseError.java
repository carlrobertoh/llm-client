package ee.carlrobert.openai.client;

import ee.carlrobert.openai.client.completion.ErrorDetails;

public interface BaseApiResponseError {

  ErrorDetails getError();
}
