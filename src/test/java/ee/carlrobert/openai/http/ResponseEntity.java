package ee.carlrobert.openai.http;

public class ResponseEntity {

  private final int statusCode;
  private final String response;

  public ResponseEntity(String response) {
    this(200, response);
  }

  public ResponseEntity(int statusCode, String response) {
    this.statusCode = statusCode;
    this.response = response;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getResponse() {
    return response;
  }
}
