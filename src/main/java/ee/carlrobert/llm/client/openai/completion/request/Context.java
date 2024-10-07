package ee.carlrobert.llm.client.openai.completion.request;

import java.util.List;

public class Context {

  private final List<FileContext> files;

  public Context(List<FileContext> files) {
    this.files = files;
  }

  public List<FileContext> getFiles() {
    return files;
  }
}
