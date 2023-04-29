package http.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import http.RequestEntity;

@FunctionalInterface
public interface BasicHttpExchange {

  String getResponse(RequestEntity request) throws JsonProcessingException;
}
