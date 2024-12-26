package ee.carlrobert.llm.client.codegpt.request.prediction;

import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import java.util.List;

public class PredictionRequest {

  private String currentRevision;
  private int cursorOffset;
  private String gitChanges;
  private String customPrompt;
  private List<FileDetails> openFiles;
  private List<OpenAIChatCompletionMessage> conversationMessages;

  public String getCurrentRevision() {
    return currentRevision;
  }

  public void setCurrentRevision(String currentRevision) {
    this.currentRevision = currentRevision;
  }

  public int getCursorOffset() {
    return cursorOffset;
  }

  public void setCursorOffset(int cursorOffset) {
    this.cursorOffset = cursorOffset;
  }

  public String getGitChanges() {
    return gitChanges;
  }

  public void setGitChanges(String gitChanges) {
    this.gitChanges = gitChanges;
  }

  public String getCustomPrompt() {
    return customPrompt;
  }

  public void setCustomPrompt(String customPrompt) {
    this.customPrompt = customPrompt;
  }

  public List<FileDetails> getOpenFiles() {
    return openFiles;
  }

  public void setOpenFiles(List<FileDetails> openFiles) {
    this.openFiles = openFiles;
  }

  public List<OpenAIChatCompletionMessage> getConversationMessages() {
    return conversationMessages;
  }

  public void setConversationMessages(List<OpenAIChatCompletionMessage> conversationMessages) {
    this.conversationMessages = conversationMessages;
  }
}