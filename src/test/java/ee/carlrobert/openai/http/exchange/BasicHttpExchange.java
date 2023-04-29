package ee.carlrobert.openai.http.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.openai.http.RequestEntity;

@FunctionalInterface
public interface BasicHttpExchange {

  String getResponse(RequestEntity request) throws JsonProcessingException;
}
