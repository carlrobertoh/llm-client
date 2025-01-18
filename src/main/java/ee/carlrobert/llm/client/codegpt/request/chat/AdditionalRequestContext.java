package ee.carlrobert.llm.client.codegpt.request.chat;

import java.util.ArrayList;
import java.util.List;

public class AdditionalRequestContext {

  private final List<ContextFile> files;

  public AdditionalRequestContext(List<ContextFile> files) {
    this.files = files;
  }

  public List<ContextFile> getFiles() {
    return new ArrayList<>(files);
  }
}
