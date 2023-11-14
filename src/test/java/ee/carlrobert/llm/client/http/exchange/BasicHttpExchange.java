package ee.carlrobert.llm.client.http.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.http.RequestEntity;
import ee.carlrobert.llm.client.http.ResponseEntity;

@FunctionalInterface
public interface BasicHttpExchange extends Exchange {

  ResponseEntity getResponse(RequestEntity request) throws JsonProcessingException;
}
