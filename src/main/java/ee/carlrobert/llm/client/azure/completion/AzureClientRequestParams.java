package ee.carlrobert.llm.client.azure.completion;

public class AzureClientRequestParams {

  private final String resourceName;
  private final String deploymentId;
  private final String apiVersion;

  public AzureClientRequestParams(String resourceName, String deploymentId, String apiVersion) {
    this.resourceName = resourceName;
    this.deploymentId = deploymentId;
    this.apiVersion = apiVersion;
  }

  public String getResourceName() {
    return resourceName;
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public String getApiVersion() {
    return apiVersion;
  }
}
