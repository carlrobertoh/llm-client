package ee.carlrobert.llm.client.codegpt.request.chat;

import java.util.ArrayList;
import java.util.List;

public class AdditionalRequestContext {

  private final List<ContextFile> files;
  private final String conversationsHistory;

  public AdditionalRequestContext(List<ContextFile> files, String conversationsHistory) {
    this.files = files;
    this.conversationsHistory = conversationsHistory;
  }

  public List<ContextFile> getFiles() {
    return new ArrayList<>(files);
  }

  public String getConversationsHistory() {
    return conversationsHistory;
  }
}
