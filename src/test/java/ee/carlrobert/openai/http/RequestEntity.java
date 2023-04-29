package ee.carlrobert.openai.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class RequestEntity {

  private final URI uri;
  private final Map<String, Object> body;
  private final Headers headers;
  private final String method;

  public RequestEntity(HttpExchange exchange) throws IOException {
    this.uri = exchange.getRequestURI();
    this.body = toMap(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8));
    this.headers = exchange.getRequestHeaders();
    this.method = exchange.getRequestMethod();
  }

  public URI getUri() {
    return uri;
  }

  public Map<String, Object> getBody() {
    return body;
  }

  private Map<String, Object> toMap(String json) throws IOException {
    if (json == null || json.isEmpty()) {
      return Collections.emptyMap();
    }

    return new ObjectMapper().readValue(json, new TypeReference<>() {});
  }

  public Headers getHeaders() {
    return headers;
  }

  public String getMethod() {
    return method;
  }
}
