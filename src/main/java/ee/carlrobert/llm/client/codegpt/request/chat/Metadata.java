package ee.carlrobert.llm.client.codegpt.request.chat;

public class Metadata {

  private final String pluginVersion;
  private final String platformVersion;

  public Metadata(String pluginVersion, String platformVersion) {
    this.pluginVersion = pluginVersion;
    this.platformVersion = platformVersion;
  }

  public String getPluginVersion() {
    return pluginVersion;
  }

  public String getPlatformVersion() {
    return platformVersion;
  }
}
