package ee.carlrobert.llm.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.Response;

public class DeserializationUtil {

  private DeserializationUtil() {
  }

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  public static <T> T mapResponse(Response response, Class<T> clazz) {
    var body = response.body();
    try {
      return OBJECT_MAPPER.readValue(body.string(), clazz);
    } catch (IOException ex) {
      throw new RuntimeException("Could not deserialize response", ex);
    }
  }
}
