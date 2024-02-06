package ee.carlrobert.llm.client.ollama.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/*
 * See <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#pull-a-model">ollama/api</a>
 */
@JsonInclude(Include.NON_NULL)
public class OllamaPullRequest {

  private final String name;
  private final boolean stream;

  public OllamaPullRequest(String name, boolean stream) {
    this.name = name;
    this.stream = stream;
  }

  public String getName() {
    return name;
  }

  public boolean isStream() {
    return stream;
  }
}
