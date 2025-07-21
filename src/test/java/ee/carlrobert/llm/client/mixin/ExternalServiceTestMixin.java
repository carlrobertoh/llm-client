package ee.carlrobert.llm.client.mixin;


import static ee.carlrobert.llm.client.mixin.ExternalService.ANTHROPIC;
import static ee.carlrobert.llm.client.mixin.ExternalService.AZURE;
import static ee.carlrobert.llm.client.mixin.ExternalService.CODEGPT;
import static ee.carlrobert.llm.client.mixin.ExternalService.GOOGLE;
import static ee.carlrobert.llm.client.mixin.ExternalService.LLAMA;
import static ee.carlrobert.llm.client.mixin.ExternalService.MISTRAL;
import static ee.carlrobert.llm.client.mixin.ExternalService.OLLAMA;
import static ee.carlrobert.llm.client.mixin.ExternalService.OPENAI;
import static ee.carlrobert.llm.client.mixin.ExternalService.YOU;
import static ee.carlrobert.llm.client.mixin.ExternalServiceTestMixin.Private.externalServiceServerMap;

import ee.carlrobert.llm.client.http.LocalCallbackServer;
import ee.carlrobert.llm.client.http.exchange.BasicHttpExchange;
import ee.carlrobert.llm.client.http.exchange.Exchange;
import ee.carlrobert.llm.client.http.exchange.NdJsonStreamHttpExchange;
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange;
import ee.carlrobert.llm.client.http.expectation.BasicExpectation;
import ee.carlrobert.llm.client.http.expectation.Expectation;
import ee.carlrobert.llm.client.http.expectation.NdJsonStreamExpectation;
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

  default void expectCodeGPT(Exchange exchange) {
    addExpectation(CODEGPT, exchange);
  }

  default void expectOpenAI(Exchange exchange) {
    addExpectation(OPENAI, exchange);
  }

  default void expectAnthropic(Exchange exchange) {
    addExpectation(ANTHROPIC, exchange);
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

  default void expectOllama(Exchange exchange) {
    addExpectation(OLLAMA, exchange);
  }

  default void expectGoogle(Exchange exchange) {
    addExpectation(GOOGLE, exchange);
  }

  default void expectMistral(Exchange exchange) {
    addExpectation(MISTRAL, exchange);
  }

  private void addExpectation(ExternalService externalService, Exchange exchange) {
    Expectation expectation;
    if (exchange instanceof StreamHttpExchange) {
      expectation = new StreamExpectation(externalService, (StreamHttpExchange) exchange);
    } else if (exchange instanceof NdJsonStreamHttpExchange) {
      expectation = new NdJsonStreamExpectation(externalService,
          (NdJsonStreamHttpExchange) exchange);
    } else {
      expectation = new BasicExpectation(externalService, (BasicHttpExchange) exchange);
    }
    externalServiceServerMap.get(externalService)
        .addExpectation(expectation);
  }
}
