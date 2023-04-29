package ee.carlrobert.openai.http.expectation;

import ee.carlrobert.openai.http.exchange.BasicHttpExchange;

public class BasicExpectation extends Expectation {

  private final BasicHttpExchange exchange;

  public BasicExpectation(String path, BasicHttpExchange exchange) {
    super(path);
    this.exchange = exchange;
  }

  public BasicHttpExchange getExchange() {
    return exchange;
  }
}
