package ee.carlrobert.openai.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ee.carlrobert.openai.http.expectation.BasicExpectation;
import ee.carlrobert.openai.http.expectation.Expectation;
import ee.carlrobert.openai.http.expectation.StreamExpectation;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalCallbackServer {

  private static final Logger LOG = LoggerFactory.getLogger(LocalCallbackServer.class);

  private final AtomicInteger currentExpectationIndex = new AtomicInteger();
  private final List<Expectation> expectations = new CopyOnWriteArrayList<>();
  private final HttpServer server;

  public LocalCallbackServer() {
    try {
      server = HttpServer.create(new InetSocketAddress(8000), 0);
    } catch (IOException e) {
      throw new RuntimeException("Could not create HttpServer", e);
    }
    server.setExecutor(null);
    server.createContext("/", exchange -> {
      try {
        var expectation = expectations.get(currentExpectationIndex.getAndIncrement());
        if (!expectation.getPath().equals(exchange.getRequestURI().getPath())) {
          try {
            throw new AssertionError(
                String.format("Expecting request path to be \"%s\", but received \"%s\"",
                    expectation.getPath(),
                    exchange.getRequestURI().getPath()));
          } catch (AssertionError e) {
            e.printStackTrace();
            throw e;
          } finally {
            exchange.sendResponseHeaders(500, -1);
            exchange.getRequestBody().close();
          }
        }
        if (expectation instanceof StreamExpectation) {
          handleStreamExchange((StreamExpectation) expectation, exchange);
        } else {
          handleExchange((BasicExpectation) expectation, exchange);
        }
      } catch (Throwable e) {
        LOG.error("Request failed", e);
        throw e;
      } finally {
        exchange.close();
      }
    });
    server.start();
  }

  public void addExpectation(Expectation expectation) {
    expectations.add(expectation);
  }

  public void stop() {
    server.stop(0);
  }

  private void handleExchange(
      BasicExpectation expectation, HttpExchange exchange) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "application/json");

    var response = expectation.getExchange().getResponse(new RequestEntity(exchange));
    exchange.sendResponseHeaders(200, response.length());

    var responseBody = exchange.getResponseBody();
    responseBody.write(response.getBytes());
    responseBody.flush();
    responseBody.close();
  }

  private void handleStreamExchange(
      StreamExpectation expectation, HttpExchange exchange) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
    exchange.getResponseHeaders().add("Cache-Control", "no-cache");
    exchange.getResponseHeaders().add("Connection", "keep-alive");
    exchange.sendResponseHeaders(200, 0);

    var responseBody = exchange.getResponseBody();

    for (var event : expectation.getExchange().getResponse(new RequestEntity(exchange))) {
      responseBody.write((String.format("data: %s\n\n", event)).getBytes());
      sleep(250);
    }

    responseBody.write(("data: [DONE]\n\n").getBytes());
    responseBody.flush();
    responseBody.close();
  }

  private static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
