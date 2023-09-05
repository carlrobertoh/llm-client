package ee.carlrobert.llm.client.http.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.carlrobert.llm.client.http.RequestEntity;
import java.util.List;

@FunctionalInterface
public interface StreamHttpExchange {

  List<String> getResponse(RequestEntity request) throws JsonProcessingException;
}
