package ee.carlrobert.llm.client.ollama.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import ee.carlrobert.llm.client.StreamableRequest;

/*
 * See <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#pull-a-model">ollama/api</a>
 */
@JsonInclude(Include.NON_NULL)
public class OllamaPullRequest implements StreamableRequest {

  private final String name;
  private final Boolean stream;


  public OllamaPullRequest(String name, Boolean stream) {
    this.name = name;
    this.stream = stream;
  }

  public String getName() {
    return name;
  }

  public Boolean getStream() {
    return stream;
  }

  @Override
  public Boolean isStream() {
    return stream;
  }
}
