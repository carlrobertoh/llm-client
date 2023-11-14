package ee.carlrobert.llm.client.http.expectation;

import ee.carlrobert.llm.client.http.Service;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;

public class StreamExpectation extends Expectation {

  private final StreamHttpExchange exchange;

  public StreamExpectation(Service service, StreamHttpExchange exchange) {
    super(service);
    this.exchange = exchange;
  }

  public StreamHttpExchange getExchange() {
    return exchange;
  }
}
