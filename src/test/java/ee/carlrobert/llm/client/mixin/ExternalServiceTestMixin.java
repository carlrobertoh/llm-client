package ee.carlrobert.llm.client.mixin;


import static ee.carlrobert.llm.client.mixin.ExternalService.AZURE;
import static ee.carlrobert.llm.client.mixin.ExternalService.LLAMA;
import static ee.carlrobert.llm.client.mixin.ExternalService.OPENAI;
import static ee.carlrobert.llm.client.mixin.ExternalService.YOU;
import static ee.carlrobert.llm.client.mixin.ExternalServiceTestMixin.Private.externalServiceServerMap;

import ee.carlrobert.llm.client.http.LocalCallbackServer;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.Exchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.http.expectation.BasicExpectation;
import ee.carlrobert.llm.client.http.expectation.StreamExpectation;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public interface ExternalServiceTestMixin {

  class Private {

    static Map<ExternalService, LocalCallbackServer> externalServiceServerMap;
  }

  static void init() {
    externalServiceServerMap = Arrays.stream(ExternalService.values())
        .collect(Collectors.toMap(
            (service) -> service,
            (service) -> {
              var server = new LocalCallbackServer(service);
              System.setProperty(
                  service.getUrlProperty(),
                  "http://localhost" + ":" + server.getPort());
              return server;
            }));
  }

  static void clearAll() {
    externalServiceServerMap.values().forEach(LocalCallbackServer::clear);
  }

  default void expectOpenAI(Exchange exchange) {
    addExpectation(OPENAI, exchange);
  }

  default void expectAzure(Exchange exchange) {
    addExpectation(AZURE, exchange);
  }

  default void expectYou(Exchange exchange) {
    addExpectation(YOU, exchange);
  }

  default void expectLlama(Exchange exchange) {
    addExpectation(LLAMA, exchange);
  }

  private void addExpectation(ExternalService externalService, Exchange exchange) {
    externalServiceServerMap.get(externalService)
        .addExpectation(exchange instanceof BasicHttpExchange
            ? new BasicExpectation(externalService, (BasicHttpExchange) exchange)
            : new StreamExpectation(externalService, (StreamHttpExchange) exchange));
  }
}
