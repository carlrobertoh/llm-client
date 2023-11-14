package ee.carlrobert.llm.client.http.expectation;

import ee.carlrobert.llm.client.http.Service;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;

public class BasicExpectation extends Expectation {

  private final BasicHttpExchange exchange;

  public BasicExpectation(Service service, BasicHttpExchange exchange) {
    super(service);
    this.exchange = exchange;
  }

  public BasicHttpExchange getExchange() {
    return exchange;
  }
}
