package ee.carlrobert.llm.client.codegpt.request;

import ee.carlrobert.llm.client.codegpt.request.chat.Metadata;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;
import java.util.UUID;

public class InlineEditRequest implements CompletionRequest {

  private final UUID sessionId;
  private final List<ConversationMessage> conversation;
  private final String selectedText;
  private final String filePath;
  private final String fileContent;
  private final int cursorOffset;
  private final String projectBasePath;
  private final List<ContextFile> contextFiles;
  private final String gitDiff;
  private final List<List<ConversationMessage>> conversationHistory;
  private final String diagnosticsInfo;
  private final String systemTemplate;
  private final String model;
  private final Metadata metadata;

  private InlineEditRequest(Builder builder) {
    this.sessionId = builder.sessionId;
    this.conversation = builder.conversation;
    this.selectedText = builder.selectedText;
    this.filePath = builder.filePath;
    this.fileContent = builder.fileContent;
    this.cursorOffset = builder.cursorOffset;
    this.projectBasePath = builder.projectBasePath;
    this.contextFiles = builder.contextFiles;
    this.gitDiff = builder.gitDiff;
    this.conversationHistory = builder.conversationHistory;
    this.diagnosticsInfo = builder.diagnosticsInfo;
    this.systemTemplate = builder.systemTemplate;
    this.model = builder.model;
    this.metadata = builder.metadata;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public List<ConversationMessage> getConversation() {
    return conversation;
  }

  public String getSelectedText() {
    return selectedText;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getFileContent() {
    return fileContent;
  }

  public String getProjectBasePath() {
    return projectBasePath;
  }

  public List<ContextFile> getContextFiles() {
    return contextFiles;
  }

  public String getGitDiff() {
    return gitDiff;
  }

  public List<List<ConversationMessage>> getConversationHistory() {
    return conversationHistory;
  }

  public String getDiagnosticsInfo() {
    return diagnosticsInfo;
  }

  public String getSystemTemplate() {
    return systemTemplate;
  }

  public String getModel() {
    return model;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public int getCursorOffset() {
    return cursorOffset;
  }

  public static class Builder {

    private final String systemTemplate;
    private final String model;

    private UUID sessionId;
    private List<ConversationMessage> conversation;
    private String selectedText;
    private String filePath;
    private String fileContent;
    private int cursorOffset;
    private String projectBasePath;
    private List<ContextFile> contextFiles;
    private String gitDiff;
    private List<List<ConversationMessage>> conversationHistory;
    private String diagnosticsInfo;
    private Metadata metadata;

    public Builder(String model, String systemTemplate) {
      this.model = model;
      this.systemTemplate = systemTemplate;
    }

    public Builder setSessionId(UUID sessionId) {
      this.sessionId = sessionId;
      return this;
    }

    public Builder setConversation(List<ConversationMessage> conversation) {
      this.conversation = conversation;
      return this;
    }

    public Builder setSelectedText(String selectedText) {
      this.selectedText = selectedText;
      return this;
    }

    public Builder setFilePath(String filePath) {
      this.filePath = filePath;
      return this;
    }

    public Builder setFileContent(String fileContent) {
      this.fileContent = fileContent;
      return this;
    }

    public Builder setCursorOffset(int cursorOffset) {
      this.cursorOffset = cursorOffset;
      return this;
    }

    public Builder setProjectBasePath(String projectBasePath) {
      this.projectBasePath = projectBasePath;
      return this;
    }

    public Builder setContextFiles(List<ContextFile> contextFiles) {
      this.contextFiles = contextFiles;
      return this;
    }

    public Builder setGitDiff(String gitDiff) {
      this.gitDiff = gitDiff;
      return this;
    }

    public Builder setConversationHistory(List<List<ConversationMessage>> conversationHistory) {
      this.conversationHistory = conversationHistory;
      return this;
    }

    public Builder setDiagnosticsInfo(String diagnosticsInfo) {
      this.diagnosticsInfo = diagnosticsInfo;
      return this;
    }


    public Builder setMetadata(Metadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public InlineEditRequest build() {
      return new InlineEditRequest(this);
    }
  }

  public static class ContextFile {

    private final String name;
    private final String path;
    private final String content;

    public ContextFile(String name, String path, String content) {
      this.name = name;
      this.path = path;
      this.content = content;
    }

    public String getName() {
      return name;
    }

    public String getPath() {
      return path;
    }

    public String getContent() {
      return content;
    }
  }

  public static class ConversationMessage {

    private final String role;
    private final String content;

    public ConversationMessage(String role, String content) {
      this.role = role;
      this.content = content;
    }

    public String getRole() {
      return role;
    }

    public String getContent() {
      return content;
    }
  }
}