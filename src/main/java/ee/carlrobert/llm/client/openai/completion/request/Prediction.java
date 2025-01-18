package ee.carlrobert.llm.client.openai.completion.request;

public class Prediction {

  private final String type;
  private final String content;

  public Prediction(String type, String content) {
    this.type = type;
    this.content = content;
  }

  public String getType() {
    return type;
  }

  public String getContent() {
    return content;
  }
}
