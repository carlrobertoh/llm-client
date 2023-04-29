package http.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import http.RequestEntity;
import java.util.List;

@FunctionalInterface
public interface StreamHttpExchange {

  List<String> getResponse(RequestEntity request) throws JsonProcessingException;
}
