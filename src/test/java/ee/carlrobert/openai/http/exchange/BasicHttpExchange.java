package ee.carlrobert.openai.http.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.openai.http.RequestEntity;
import ee.carlrobert.openai.http.ResponseEntity;

@FunctionalInterface
public interface BasicHttpExchange {

  ResponseEntity getResponse(RequestEntity request) throws JsonProcessingException;
}
