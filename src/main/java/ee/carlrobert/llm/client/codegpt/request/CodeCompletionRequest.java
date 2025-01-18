package ee.carlrobert.llm.client.codegpt.request;

import ee.carlrobert.llm.completion.CompletionRequest;
import java.util.List;
import java.util.UUID;

public class CodeCompletionRequest implements CompletionRequest {

  private final String model;
  private final String prefix;
  private final String suffix;
  private final String fileExtension;
  private final String fileContent;
  private final int cursorOffset;
  private final List<String> stop;
  private final UUID sessionId;
  private final String platformVersion;
  private final String pluginVersion;

  protected CodeCompletionRequest(Builder builder) {
    this.model = builder.model;
    this.prefix = builder.prefix;
    this.suffix = builder.suffix;
    this.fileExtension = builder.fileExtension;
    this.fileContent = builder.fileContent;
    this.cursorOffset = builder.cursorOffset;
    this.stop = builder.stop;
    this.sessionId = builder.sessionId;
    this.platformVersion = builder.platformVersion;
    this.pluginVersion = builder.pluginVersion;
  }

  public String getModel() {
    return model;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public String getFileContent() {
    return fileContent;
  }

  public int getCursorOffset() {
    return cursorOffset;
  }

  public List<String> getStop() {
    return stop;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public String getPlatformVersion() {
    return platformVersion;
  }

  public String getPluginVersion() {
    return pluginVersion;
  }

  public static class Builder {

    private String model = "codestral";
    private String prefix;
    private String suffix;
    private String fileExtension;
    private String fileContent;
    private int cursorOffset;
    private List<String> stop;
    private UUID sessionId;
    private String platformVersion;
    private String pluginVersion;

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setPrefix(String prefix) {
      this.prefix = prefix;
      return this;
    }

    public Builder setSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    public Builder setFileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
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

    public Builder setStop(List<String> stop) {
      this.stop = stop;
      return this;
    }

    public Builder setSessionId(UUID sessionId) {
      this.sessionId = sessionId;
      return this;
    }

    public Builder setPluginVersion(String pluginVersion) {
      this.pluginVersion = pluginVersion;
      return this;
    }

    public Builder setPlatformVersion(String platformVersion) {
      this.platformVersion = platformVersion;
      return this;
    }

    public CodeCompletionRequest build() {
      return new CodeCompletionRequest(this);
    }
  }
}