package ee.carlrobert.llm.client;

import ee.carlrobert.llm.client.http.LocalCallbackServer;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.http.expectation.BasicExpectation;
import ee.carlrobert.llm.client.http.expectation.StreamExpectation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

abstract class BaseTest {

  protected LocalCallbackServer server;

  @BeforeEach
  void init() {
    server = new LocalCallbackServer();
  }

  @AfterEach
  void tearDown() {
    server.stop();
  }

  protected void expectRequest(String path, BasicHttpExchange exchange) {
    server.addExpectation(new BasicExpectation(path, exchange));
  }

  protected void expectStreamRequest(String path, StreamHttpExchange exchange) {
    server.addExpectation(new StreamExpectation(path, exchange));
  }
}
