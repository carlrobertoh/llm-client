package ee.carlrobert.llm.client.codegpt.response;

public class PredictionResponse {

  private String nextRevision;

  public String getNextRevision() {
    return nextRevision;
  }

  public void setNextRevision(String nextRevision) {
    this.nextRevision = nextRevision;
  }
}
