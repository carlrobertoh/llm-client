package ee.carlrobert.llm.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException;
import java.io.IOException;
import okhttp3.Response;

public class DeserializationUtil {

  private DeserializationUtil() {
  }

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  public static <T> T mapResponse(Response response, Class<T> clazz) {
    var body = response.body();
    if (body == null) {
      throw new RuntimeException("Response body is null");
    }

    String json = "";
    try {
      json = body.string();
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (IOException ex) {
      try {
        throw OBJECT_MAPPER.readValue(json, CodeGPTException.class);
      } catch (IOException e) {
        throw new RuntimeException("Could not deserialize response", ex);
      }
    }
  }
}
