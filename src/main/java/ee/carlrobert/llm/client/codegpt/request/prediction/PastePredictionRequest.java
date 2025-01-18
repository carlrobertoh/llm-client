package ee.carlrobert.llm.client.codegpt.request.prediction;

public class PastePredictionRequest extends PredictionRequest {

  private final String pastedCode;

  public PastePredictionRequest(String pastedCode) {
    this.pastedCode = pastedCode;
  }

  public String getPastedCode() {
    return pastedCode;
  }
}
