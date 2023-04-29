package http.expectation;

import http.exchange.StreamHttpExchange;

public class StreamExpectation extends Expectation {

  private final StreamHttpExchange exchange;

  public StreamExpectation(String path, StreamHttpExchange exchange) {
    super(path);
    this.exchange = exchange;
  }

  public StreamHttpExchange getExchange() {
    return exchange;
  }
}
