package ee.carlrobert.llm.client.codegpt.request.prediction;

public class AutocompletionPredictionRequest extends PredictionRequest {

  private String appliedCompletion;
  private String previousRevision;

  public String getAppliedCompletion() {
    return appliedCompletion;
  }

  public void setAppliedCompletion(String appliedCompletion) {
    this.appliedCompletion = appliedCompletion;
  }

  public String getPreviousRevision() {
    return previousRevision;
  }

  public void setPreviousRevision(String previousRevision) {
    this.previousRevision = previousRevision;
  }
}