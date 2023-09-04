package ee.carlrobert.llm.client.http.expectation;

public class Expectation {

  private final String path;

  public Expectation(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
