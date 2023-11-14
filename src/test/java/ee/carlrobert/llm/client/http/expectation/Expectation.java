package ee.carlrobert.llm.client.http.expectation;

import ee.carlrobert.llm.client.http.Service;

public class Expectation {

  private final Service service;

  public Expectation(Service service) {
    this.service = service;
  }

  public Service getService() {
    return service;
  }
}
