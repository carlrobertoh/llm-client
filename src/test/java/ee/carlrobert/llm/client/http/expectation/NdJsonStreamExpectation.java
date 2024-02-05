package ee.carlrobert.llm.client.http.expectation;

import ee.carlrobert.llm.client.http.Service;
import ee.carlrobert.llm.client.http.exchange.NdJsonStreamHttpExchange;

public class NdJsonStreamExpectation extends Expectation {

  private final NdJsonStreamHttpExchange exchange;

  public NdJsonStreamExpectation(Service service, NdJsonStreamHttpExchange exchange) {
    super(service);
    this.exchange = exchange;
  }

  public NdJsonStreamHttpExchange getExchange() {
    return exchange;
  }
}
