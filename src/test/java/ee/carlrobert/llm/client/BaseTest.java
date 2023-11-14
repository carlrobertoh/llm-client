package ee.carlrobert.llm.client;

import ee.carlrobert.llm.client.mixin.ExternalServiceTestMixin;
import org.junit.jupiter.api.AfterEach;

public abstract class BaseTest implements ExternalServiceTestMixin {

  static {
    ExternalServiceTestMixin.init();
  }

  @AfterEach
  public void cleanUpEach() {
    ExternalServiceTestMixin.clearAll();
  }
}
